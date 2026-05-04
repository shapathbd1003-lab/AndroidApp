package com.example.serviceapp.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.serviceapp.data.model.Job
import com.example.serviceapp.data.model.Provider
import com.example.serviceapp.data.model.ServiceHistory
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.ServiceData
import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FakeRepository {

    var provider   by mutableStateOf<Provider?>(null)
    val jobs        = mutableStateListOf<Job>()
    var loggedIn   by mutableStateOf(false)

    private val auth: FirebaseAuth      get() = FirebaseAuth.getInstance()
    private val db:   FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private var pendingListener:  ListenerRegistration? = null  // all pending jobs for this service
    private var myJobsListener:   ListenerRegistration? = null  // provider's own in-progress jobs
    private var requestsListener: ListenerRegistration? = null  // kept for compat
    private var approvalListener: ListenerRegistration? = null

    // Intermediate maps to merge both listeners
    private val pendingJobs = mutableMapOf<String, Job>()
    private val myJobs      = mutableMapOf<String, Job>()

    // Service type IDs — used by RegisterScreen chips
    val serviceTypes get() = ServiceData.categories.map { it.id }

    // ── Register ─────────────────────────────────────────────────────────────
    suspend fun register(
        name: String, phone: String, email: String, password: String,
        nid: String, photo: String, baseFee: Double,
        serviceType: String, certificate: String,
        skillLevel: String = "general"
    ): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid    = result.user?.uid ?: error("No user ID returned")

        db.collection("providers").document(uid).set(hashMapOf<String, Any?>(
            "name"         to name.trim(),
            "phone"        to phone.trim(),
            "email"        to email.trim(),
            "nid"          to nid.trim(),
            "serviceType"  to serviceType,
            "baseFee"      to baseFee,
            "photo"        to "",
            "certificate"  to "",
            "availability" to "available",
            "rating"       to 4.5,
            "skillLevel"   to skillLevel,
            "points"       to 500,
            "isApproved"   to null,
            "createdAt"    to FieldValue.serverTimestamp()
        )).await()

        provider = Provider(
            id = uid, name = name.trim(), phone = phone.trim(),
            email = email.trim(), photo = photo,
            nid = nid.trim(), baseFee = baseFee,
            serviceType = serviceType, certificate = certificate,
            skillLevel = skillLevel,
            isApproved = null
        )
        loggedIn = true
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val uid = auth.signInWithEmailAndPassword(email.trim(), password).await()
            .user?.uid ?: error("No user ID returned")
        loadProviderFromFirestore(uid)
        loggedIn = true
        if (provider?.isApproved == true) startListeningToRequests()
    }

    // ── Auto-restore session ──────────────────────────────────────────────────
    suspend fun loadCurrentUser(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return runCatching {
            loadProviderFromFirestore(uid)
            loggedIn = true
            if (provider?.isApproved == true) startListeningToRequests()
        }.isSuccess
    }

    private suspend fun loadProviderFromFirestore(uid: String) {
        val doc = db.collection("providers").document(uid).get().await()
        val approvedRaw = doc.get("isApproved")
        provider = Provider(
            id           = uid,
            name         = doc.getString("name")         ?: "",
            phone        = doc.getString("phone")        ?: "",
            email        = doc.getString("email")        ?: "",
            photo        = doc.getString("photo")        ?: "",
            nid          = doc.getString("nid")          ?: "",
            baseFee      = doc.getDouble("baseFee")      ?: 300.0,
            serviceType  = doc.getString("serviceType")  ?: "",
            certificate  = doc.getString("certificate")  ?: "",
            availability = doc.getString("availability") ?: "available",
            rating       = doc.getDouble("rating")       ?: 4.5,
            skillLevel   = doc.getString("skillLevel")   ?: "general",
            advance      = doc.getDouble("advance")      ?: 0.0,
            points       = (doc.getLong("points")        ?: 500).toInt(),
            isApproved   = if (approvedRaw == null) null else approvedRaw as? Boolean
        )
    }

    // ── Real-time listeners ───────────────────────────────────────────────────
    fun startListeningToRequests() {
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
        val allowed = ServiceData.allowedTypes(p.skillLevel)

        // Listener 1: all PENDING jobs for this service type (to accept)
        pendingListener?.remove()
        pendingListener = db.collection("requests")
            .whereEqualTo("serviceType", p.serviceType)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snaps, _ ->
                pendingJobs.clear()
                snaps?.documents?.forEach { doc ->
                    val problemType = doc.getString("problemType") ?: "normal"
                    if (problemType !in allowed) return@forEach
                    pendingJobs[doc.id] = Job(
                        id          = doc.id,
                        description = AppStrings.serviceTypeName(doc.getString("serviceType") ?: ""),
                        address     = doc.getString("address")     ?: "",
                        phone       = doc.getString("clientPhone") ?: "",
                        overview    = doc.getString("description") ?: "",
                        problemType = problemType,
                        status      = "pending",
                        lat         = doc.getDouble("lat") ?: 0.0,
                        lng         = doc.getDouble("lng") ?: 0.0
                    )
                }
                rebuildJobList()
            }

        // Listener 2: provider's own jobs (awaiting client / accepted / completed)
        myJobsListener?.remove()
        myJobsListener = db.collection("requests")
            .whereEqualTo("providerId", uid)
            .addSnapshotListener { snaps, _ ->
                myJobs.clear()
                snaps?.documents?.forEach { doc ->
                    when (val status = doc.getString("status") ?: return@forEach) {
                        "awaiting_approval" -> {
                            myJobs[doc.id] = Job(
                                id          = doc.id,
                                description = AppStrings.serviceTypeName(doc.getString("serviceType") ?: ""),
                                address     = doc.getString("address")     ?: "",
                                phone       = doc.getString("clientPhone") ?: "",
                                overview    = doc.getString("description") ?: "",
                                problemType = doc.getString("problemType") ?: "normal",
                                status      = "awaiting",   // waiting for client decision
                                lat         = doc.getDouble("lat") ?: 0.0,
                                lng         = doc.getDouble("lng") ?: 0.0
                            )
                        }
                        "accepted" -> {
                            myJobs[doc.id] = Job(
                                id          = doc.id,
                                description = AppStrings.serviceTypeName(doc.getString("serviceType") ?: ""),
                                address     = doc.getString("address")     ?: "",
                                phone       = doc.getString("clientPhone") ?: "",
                                overview    = doc.getString("description") ?: "",
                                problemType = doc.getString("problemType") ?: "normal",
                                status      = "agreed",
                                lat         = doc.getDouble("lat") ?: 0.0,
                                lng         = doc.getDouble("lng") ?: 0.0
                            )
                        }
                        "on_the_way" -> {
                            myJobs[doc.id] = Job(
                                id          = doc.id,
                                description = AppStrings.serviceTypeName(doc.getString("serviceType") ?: ""),
                                address     = doc.getString("address")     ?: "",
                                phone       = doc.getString("clientPhone") ?: "",
                                overview    = doc.getString("description") ?: "",
                                problemType = doc.getString("problemType") ?: "normal",
                                status      = "on_the_way",
                                lat         = doc.getDouble("lat") ?: 0.0,
                                lng         = doc.getDouble("lng") ?: 0.0
                            )
                        }
                        "completed" -> {
                            // Client marked done — add to provider history if not already there
                            val prov = provider
                            if (prov != null && prov.history.none { it.id == doc.id }) {
                                val desc = AppStrings.serviceTypeName(doc.getString("serviceType") ?: "")
                                prov.advance += prov.baseFee
                                prov.history.add(ServiceHistory(doc.id, desc, prov.baseFee))

                                // Persist updated advance to Firestore
                                val uid = auth.currentUser?.uid
                                if (uid != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        runCatching {
                                            db.collection("providers").document(uid).update(
                                                mapOf("advance" to prov.advance)
                                            ).await()
                                        }
                                    }
                                }
                            }
                            // Completed jobs don't appear in active job list
                        }
                    }
                }
                rebuildJobList()
            }
    }

    private fun rebuildJobList() {
        val sorted = (pendingJobs.values + myJobs.values)
            .sortedWith(compareBy(
                { when (it.status) { "agreed" -> 0; "awaiting" -> 1; else -> 2 } },
                { if (it.distanceKm >= 0) it.distanceKm else Double.MAX_VALUE }
            ))
        jobs.clear()
        jobs.addAll(sorted)
    }

    // ── Approval listener — auto fires when admin approves/rejects ────────────
    fun listenForApproval(
        onApproved: () -> Unit,
        onRejected: () -> Unit
    ): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("providers").document(uid).addSnapshotListener { snap, _ ->
            val raw = snap?.get("isApproved")
            when {
                raw == true  -> {
                    provider?.isApproved = true
                    startListeningToRequests()
                    onApproved()
                }
                raw == false -> { provider?.isApproved = false; onRejected() }
            }
        }
    }

    // ── Accept a real client request ──────────────────────────────────────────
    // Returns false if insufficient points
    fun accept(job: Job): Boolean {
        val p   = provider ?: return false
        val uid = auth.currentUser?.uid ?: return false

        // Points check — need 400 to accept a job
        if (p.points < 400) return false

        // Deduct 400 points immediately
        p.points -= 400

        // Optimistic: move from pending to awaiting in local list immediately
        pendingJobs.remove(job.id)
        myJobs[job.id] = job.copy(status = "awaiting")
        rebuildJobList()

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("requests").document(job.id).update(mapOf(
                    "status"          to "awaiting_approval",
                    "providerId"      to uid,
                    "providerName"    to p.name,
                    "providerPhone"   to p.phone,
                    "providerRating"  to p.rating,
                    "providerBaseFee" to p.baseFee,
                    "acceptedAt"      to FieldValue.serverTimestamp()
                )).await()
                // Persist deducted points
                db.collection("providers").document(uid).update(mapOf("points" to p.points)).await()
            }
        }
        return true
    }

    // ── On the way ────────────────────────────────────────────────────────────
    fun markOnTheWay(jobId: String) {
        val idx = jobs.indexOfFirst { it.id == jobId }
        if (idx >= 0) jobs[idx] = jobs[idx].copy(status = "on_the_way")
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("requests").document(jobId).update(mapOf(
                    "status"       to "on_the_way",
                    "onTheWayAt"   to FieldValue.serverTimestamp()
                )).await()
            }
        }
    }

    // ── Set custom agreed price ───────────────────────────────────────────────
    fun setAgreedPrice(jobId: String, price: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("requests").document(jobId).update(mapOf("agreedPrice" to price)).await()
            }
        }
    }

    // ── Add points (called by admin) ──────────────────────────────────────────
    fun addPoints(amount: Int) {
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
        p.points += amount
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("providers").document(uid).update(mapOf("points" to p.points)).await()
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    fun logout() {
        auth.signOut()
        pendingListener?.remove();  pendingListener  = null
        myJobsListener?.remove();   myJobsListener   = null
        requestsListener?.remove(); requestsListener = null
        pendingJobs.clear(); myJobs.clear()
        loggedIn  = false
        provider  = null
        jobs.clear()
    }

    fun setAvailability(status: String) { provider?.availability = status }

    // Sort jobs by distance from provider's current location (closest first)
    fun sortByLocation(providerLat: Double, providerLng: Double) {
        if (providerLat == 0.0 && providerLng == 0.0) return
        val results = FloatArray(1)
        val withDistance = jobs.map { job ->
            if (job.lat != 0.0 && job.lng != 0.0) {
                Location.distanceBetween(providerLat, providerLng, job.lat, job.lng, results)
                job.copy(distanceKm = results[0] / 1000.0)
            } else job
        }.sortedBy { if (it.distanceKm >= 0) it.distanceKm else Double.MAX_VALUE }
        jobs.clear()
        jobs.addAll(withDistance)
    }

    fun clearHistory() {
        provider?.let { p ->
            p.history.clear()
            p.advance = 0.0
        }
    }
}
