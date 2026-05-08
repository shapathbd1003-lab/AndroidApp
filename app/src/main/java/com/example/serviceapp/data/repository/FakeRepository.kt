package com.example.serviceapp.data.repository

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.serviceapp.data.model.Job
import com.example.serviceapp.data.model.Provider
import com.example.serviceapp.data.model.ServiceHistory
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.ServiceData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object FakeRepository {

    var provider    by mutableStateOf<Provider?>(null)
    val jobs         = mutableStateListOf<Job>()
    var loggedIn    by mutableStateOf(false)
    var pointsState by mutableStateOf(500)

    private val auth: FirebaseAuth      get() = FirebaseAuth.getInstance()
    private val db:   FirebaseFirestore get() = FirebaseFirestore.getInstance()

    private var pendingListener:  ListenerRegistration? = null
    private var myJobsListener:   ListenerRegistration? = null
    private var approvalListener: ListenerRegistration? = null

    // Firestore is the single source of truth.
    // The Firestore SDK fires listeners immediately from its local write cache,
    // so all status transitions just write to Firestore and let the listeners
    // rebuild the list. No optimistic updates, no local state to go stale.
    private val pendingJobs = mutableMapOf<String, Job>()
    private val myJobs      = mutableMapOf<String, Job>()

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
            skillLevel = skillLevel, isApproved = null
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

    // ── Session restore ───────────────────────────────────────────────────────
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
        pointsState = provider?.points ?: 500
        if (doc.getLong("points") == null) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching { db.collection("providers").document(uid).update(mapOf("points" to 500)).await() }
            }
        }
    }

    // ── Real-time listeners ───────────────────────────────────────────────────
    fun startListeningToRequests() {
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
        val allowed = ServiceData.allowedTypes(p.skillLevel)

        // Listener 1: all pending jobs for this service type
        pendingListener?.remove()
        pendingListener = db.collection("requests")
            .whereEqualTo("serviceType", p.serviceType)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snaps, _ ->
                pendingJobs.clear()
                snaps?.documents?.forEach { doc ->
                    val problemType = doc.getString("problemType") ?: "normal"
                    if (problemType !in allowed) return@forEach
                    pendingJobs[doc.id] = docToJob(doc, "pending")
                }
                rebuildJobList()
            }

        // Listener 2: all jobs the provider owns (any status)
        myJobsListener?.remove()
        myJobsListener = db.collection("requests")
            .whereEqualTo("providerId", uid)
            .addSnapshotListener { snaps, _ ->
                myJobs.clear()
                snaps?.documents?.forEach { doc ->
                    when (val status = doc.getString("status") ?: return@forEach) {
                        "awaiting_approval" -> myJobs[doc.id] = docToJob(doc, "awaiting")
                        "accepted"          -> myJobs[doc.id] = docToJob(doc, "agreed")
                        "on_the_way"        -> myJobs[doc.id] = docToJob(doc, "on_the_way")
                        "arrived"           -> myJobs[doc.id] = docToJob(doc, "arrived")
                        "working"           -> myJobs[doc.id] = docToJob(doc, "working")
                        "finished", "completed" -> addToHistory(doc)
                        // cancelled variants: omit from active list
                    }
                }
                rebuildJobList()
            }
    }

    private fun docToJob(doc: DocumentSnapshot, localStatus: String) = Job(
        id          = doc.id,
        description = AppStrings.serviceTypeName(doc.getString("serviceType") ?: ""),
        address     = doc.getString("address")     ?: "",
        phone       = doc.getString("clientPhone") ?: "",
        overview    = doc.getString("description") ?: "",
        problemType = doc.getString("problemType") ?: "normal",
        status      = localStatus,
        lat         = doc.getDouble("lat") ?: 0.0,
        lng         = doc.getDouble("lng") ?: 0.0
    )

    private fun addToHistory(doc: DocumentSnapshot) {
        val prov = provider ?: return
        if (prov.history.any { it.id == doc.id }) return
        val desc         = AppStrings.serviceTypeName(doc.getString("serviceType") ?: "")
        val fee          = doc.getDouble("agreedPrice")?.takeIf { it > 0 } ?: prov.baseFee
        val clientName   = doc.getString("clientName")  ?: ""
        val clientRating = (doc.getLong("rating") ?: 0).toInt()
        prov.advance += fee
        prov.history.add(ServiceHistory(doc.id, desc, fee, clientName, clientRating))
        val uid = auth.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("providers").document(uid)
                    .update(mapOf("advance" to prov.advance)).await()
            }
        }
    }

    private fun rebuildJobList() {
        // myJobs always wins: a job the provider owns never shows as a new pending job
        val merged = myJobs + pendingJobs.filterKeys { it !in myJobs }
        val sorted = merged.values.sortedWith(compareBy(
            { when (it.status) {
                "agreed"     -> 0
                "arrived"    -> 1
                "working"    -> 1
                "on_the_way" -> 2
                "awaiting"   -> 3
                else         -> 4
            }},
            { if (it.distanceKm >= 0) it.distanceKm else Double.MAX_VALUE }
        ))
        jobs.clear()
        jobs.addAll(sorted)
    }

    // ── Approval listener ─────────────────────────────────────────────────────
    fun listenForApproval(
        onApproved: () -> Unit,
        onRejected: () -> Unit
    ): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return db.collection("providers").document(uid).addSnapshotListener { snap, _ ->
            val raw = snap?.get("isApproved")
            when {
                raw == true  -> { provider?.isApproved = true;  startListeningToRequests(); onApproved() }
                raw == false -> { provider?.isApproved = false; onRejected() }
            }
        }
    }

    // ── Job actions — just write to Firestore, listeners update the UI ────────
    fun accept(job: Job): Boolean {
        val p   = provider ?: return false
        val uid = auth.currentUser?.uid ?: return false
        if (p.points < 400) return false

        p.points -= 400
        pointsState = p.points

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
                db.collection("providers").document(uid)
                    .update(mapOf("points" to p.points)).await()
            }
        }
        return true
    }

    fun markOnTheWay(jobId: String) = writeStatus(jobId, "on_the_way", "onTheWayAt")
    fun markArrived(jobId: String)  = writeStatus(jobId, "arrived",    "arrivedAt")
    fun markWorking(jobId: String)  = writeStatus(jobId, "working",    "workingAt")

    fun markFinished(jobId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("requests").document(jobId).update(mapOf(
                    "status"     to "finished",
                    "finishedAt" to FieldValue.serverTimestamp()
                )).await()
            }
        }
    }

    private fun writeStatus(jobId: String, fsStatus: String, timeField: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("requests").document(jobId).update(mapOf(
                    "status"  to fsStatus,
                    timeField to FieldValue.serverTimestamp()
                )).await()
            }
        }
    }

    fun setAgreedPrice(jobId: String, price: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("requests").document(jobId)
                    .update(mapOf("agreedPrice" to price)).await()
            }
        }
    }

    fun addPoints(amount: Int) {
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
        p.points += amount
        pointsState = p.points
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("providers").document(uid)
                    .update(mapOf("points" to p.points)).await()
            }
        }
    }

    fun setAvailability(status: String) { provider?.availability = status }

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

    fun logout() {
        auth.signOut()
        pendingListener?.remove();  pendingListener  = null
        myJobsListener?.remove();   myJobsListener   = null
        approvalListener?.remove(); approvalListener = null
        pendingJobs.clear()
        myJobs.clear()
        loggedIn = false
        provider = null
        jobs.clear()
    }
}
