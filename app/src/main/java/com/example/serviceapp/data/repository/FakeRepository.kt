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
import com.example.serviceapp.utils.ImageUploader
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

    // Single listener replaces the old pendingListener + myJobsListener pair.
    // Two listeners caused an unfixable race: whichever fired last would
    // overwrite the other's stale data, causing status to cycle backwards.
    private var requestsListener: ListenerRegistration? = null
    private var approvalListener: ListenerRegistration? = null
    private var profileListener:  ListenerRegistration? = null

    val serviceTypes get() = ServiceData.categories.map { it.id }

    // ── Profile listener — keeps points/rating in sync with Firestore ─────────
    fun startProfileListener() {
        val uid = auth.currentUser?.uid ?: return
        profileListener?.remove()
        profileListener = db.collection("providers").document(uid).addSnapshotListener { snap, _ ->
            val p = provider ?: return@addSnapshotListener
            if (snap == null || !snap.exists()) return@addSnapshotListener
            val pts = (snap.getLong("points")        ?: p.points.toLong()).toInt()
            val rat =  snap.getDouble("rating")      ?: p.rating
            val cnt = (snap.getLong("completedJobs") ?: p.ratingCount.toLong()).toInt()
            if (pts != p.points || rat != p.rating || cnt != p.ratingCount) {
                provider = provider?.copy(points = pts, rating = rat, ratingCount = cnt)
                pointsState = pts
            }
        }
    }

    fun stopProfileListener() {
        profileListener?.remove()
        profileListener = null
    }

    // ── Register ─────────────────────────────────────────────────────────────
    suspend fun register(
        name: String, phone: String, email: String, password: String,
        nid: String, photo: String, baseFee: Double,
        serviceType: String, certificate: String,
        skillLevel: String = "general",
        coveredAreas: List<String> = emptyList()
    ): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid    = result.user?.uid ?: error("No user ID returned")
        // Upload images to Firebase Storage (no-op if already an https:// URL or empty)
        val photoUrl = ImageUploader.uploadIfLocal(uid, photo, "photo.jpg")
        val certUrl  = ImageUploader.uploadIfLocal(uid, certificate, "certificate.jpg")
        db.collection("providers").document(uid).set(hashMapOf<String, Any?>(
            "name"         to name.trim(),
            "phone"        to phone.trim(),
            "email"        to email.trim(),
            "nid"          to nid.trim(),
            "serviceType"  to serviceType,
            "baseFee"      to baseFee,
            "photo"        to photoUrl,
            "certificate"  to certUrl,
            "availability" to "available",
            "rating"       to 4.5,
            "skillLevel"   to skillLevel,
            "points"        to 500,
            "isApproved"    to null,
            "coveredAreas"  to coveredAreas,
            "createdAt"     to FieldValue.serverTimestamp()
        )).await()
        provider = Provider(
            id = uid, name = name.trim(), phone = phone.trim(),
            email = email.trim(), photo = photoUrl,
            nid = nid.trim(), baseFee = baseFee,
            serviceType = serviceType, certificate = certUrl,
            skillLevel = skillLevel, isApproved = null,
            coveredAreas = coveredAreas
        )
        loggedIn = true
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val uid = auth.signInWithEmailAndPassword(email.trim(), password).await()
            .user?.uid ?: error("No user ID returned")
        if (!loadProviderFromFirestore(uid)) error("No provider account found for this user")
        loggedIn = true
        if (provider?.isApproved == true) startListeningToRequests()
        startProfileListener()
    }

    // ── Session restore ───────────────────────────────────────────────────────
    suspend fun loadCurrentUser(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            if (!loadProviderFromFirestore(uid)) return false
            loggedIn = true
            if (provider?.isApproved == true) startListeningToRequests()
            startProfileListener()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Returns false when the providers/{uid} document does not exist (e.g. client user)
    private suspend fun loadProviderFromFirestore(uid: String): Boolean {
        val doc = db.collection("providers").document(uid).get().await()
        if (!doc.exists()) return false
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
            ratingCount  = (doc.getLong("completedJobs") ?: 0).toInt(),
            isApproved   = if (approvedRaw == null) null else approvedRaw as? Boolean,
            coveredAreas = (doc.get("coveredAreas") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
        pointsState = provider?.points ?: 500
        if (doc.getLong("points") == null) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching { db.collection("providers").document(uid).update(mapOf("points" to 500)).await() }
            }
        }
        return true
    }

    // ── Save profile changes (uploads local images to Firebase Storage first) ──
    suspend fun saveProfile(
        name: String, phone: String, email: String, nid: String,
        photo: String, baseFee: Double, certificate: String
    ): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Not logged in")
        // Upload images; non-local strings (presets, existing https URLs) pass through unchanged
        val photoUrl = ImageUploader.uploadIfLocal(uid, photo, "photo.jpg")
        val certUrl  = ImageUploader.uploadIfLocal(uid, certificate, "certificate.jpg")
        db.collection("providers").document(uid).update(mapOf(
            "name"        to name,
            "phone"       to phone,
            "email"       to email,
            "nid"         to nid,
            "photo"       to photoUrl,
            "baseFee"     to baseFee,
            "certificate" to certUrl
        )).await()
        // Reflect Storage URLs locally so UI shows correct image immediately
        provider?.let { p ->
            p.photo       = photoUrl
            p.certificate = certUrl
        }
    }

    // ── Single listener ───────────────────────────────────────────────────────
    // Queries all requests for this provider's service type — no status filter.
    // Client-side we show: pending jobs (available to accept) + own jobs (any status).
    //
    // Why single listener: with two listeners (one for pending, one for own jobs)
    // there is an unavoidable race where the snapshot from one listener is stale
    // relative to the other, causing the UI to show the wrong status. One listener,
    // one snapshot, one rebuild — no coordination needed, no race possible.
    fun startListeningToRequests() {
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
        val allowed = ServiceData.allowedTypes(p.skillLevel)

        requestsListener?.remove()
        requestsListener = db.collection("requests")
            .whereEqualTo("serviceType", p.serviceType)
            .addSnapshotListener { snaps, err ->
                if (err != null || snaps == null) return@addSnapshotListener

                val newJobs = mutableMapOf<String, Job>()
                val pAreas  = provider?.coveredAreas ?: emptyList()

                snaps.documents.forEach { doc ->
                    val status        = doc.getString("status")     ?: return@forEach
                    val docProviderId = doc.getString("providerId") ?: ""
                    val problemType   = doc.getString("problemType") ?: "normal"
                    val docArea       = doc.getString("area")        ?: ""

                    val areaMatch = pAreas.isEmpty() ||
                        pAreas.any { it.equals(docArea, ignoreCase = true) }

                    val minSkill  = doc.getString("minSkillLevel") ?: ""
                    val skillMatch = minSkill.isEmpty() || meetsSkillRequirement(p.skillLevel, minSkill)

                    when {
                        status == "pending" && docProviderId.isEmpty() &&
                        problemType in allowed && areaMatch && skillMatch -> {
                            newJobs[doc.id] = docToJob(doc, "pending")
                        }
                        docProviderId == uid -> {
                            val localStatus = when (status) {
                                "awaiting_approval" -> "awaiting"
                                "accepted"          -> "agreed"
                                "on_the_way"        -> "on_the_way"
                                "arrived"           -> "arrived"
                                "working"           -> "working"
                                "finished", "completed" -> { addToHistory(doc); null }
                                else -> null
                            }
                            if (localStatus != null) newJobs[doc.id] = docToJob(doc, localStatus)
                        }
                    }
                }

                android.util.Log.d("JobFlow", "jobs: ${newJobs.values.map { "${it.id.takeLast(6)}=${it.status}" }}")

                val sorted = newJobs.values.sortedWith(compareBy(
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
    }

    private fun meetsSkillRequirement(providerLevel: String, minRequired: String): Boolean {
        val order = listOf("general", "professional", "expert")
        return order.indexOf(providerLevel) >= order.indexOf(minRequired)
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

    // ── Job actions — write to Firestore, listener rebuilds the UI ────────────
    fun accept(job: Job): Boolean {
        val p   = provider ?: return false
        val uid = auth.currentUser?.uid ?: return false
        if (p.points < 400) return false

        p.points -= 400
        pointsState = p.points

        val docRef = db.collection("requests").document(job.id)

        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                // Transaction guarantees the job is still "pending" at the server
                // before writing. This prevents a stale-cache Accept from resetting
                // a job that has already advanced to on_the_way / arrived / etc.
                db.runTransaction { tx ->
                    val snap = tx.get(docRef)
                    if (snap.getString("status") != "pending") {
                        throw IllegalStateException("Job is no longer pending")
                    }
                    tx.update(docRef, mapOf(
                        "status"          to "awaiting_approval",
                        "providerId"      to uid,
                        "providerName"    to p.name,
                        "providerPhone"   to p.phone,
                        "providerRating"  to p.rating,
                        "providerBaseFee" to p.baseFee,
                        "acceptedAt"      to FieldValue.serverTimestamp()
                    ))
                }.await()
                db.collection("providers").document(uid)
                    .update(mapOf("points" to p.points)).await()
            }
            if (result.isFailure) {
                // Transaction failed (job was not pending) — restore deducted points
                CoroutineScope(Dispatchers.Main).launch {
                    p.points += 400
                    pointsState = p.points
                }
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

    fun updateCoveredAreas(areas: List<String>) {
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
        p.coveredAreas = areas
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                db.collection("providers").document(uid)
                    .update(mapOf("coveredAreas" to areas)).await()
            }
        }
        // Restart listener so area filter applies immediately
        startListeningToRequests()
    }

    fun clearHistory() {
        provider?.let { p ->
            p.history.clear()
            p.advance = 0.0
        }
    }

    fun logout() {
        auth.signOut()
        requestsListener?.remove();  requestsListener  = null
        approvalListener?.remove();  approvalListener  = null
        profileListener?.remove();   profileListener   = null
        loggedIn = false
        provider = null
        jobs.clear()
    }
}
