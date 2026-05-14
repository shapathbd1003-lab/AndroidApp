package com.example.serviceapp.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.serviceapp.data.model.Client
import com.example.serviceapp.data.model.ServiceRequest
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

object ClientRepository {

    var client  by mutableStateOf<Client?>(null)
    val requests = mutableStateListOf<ServiceRequest>()
    var loggedIn by mutableStateOf(false)

    private val auth: FirebaseAuth      get() = FirebaseAuth.getInstance()
    private val db:   FirebaseFirestore get() = FirebaseFirestore.getInstance()

    // ── Register ──────────────────────────────────────────────────────────────
    suspend fun register(
        name: String, phone: String, email: String, password: String, avatar: String = ""
    ): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid    = result.user?.uid ?: error("No user ID")
        db.collection("clients").document(uid).set(hashMapOf<String, Any?>(
            "name" to name.trim(), "phone" to phone.trim(), "email" to email.trim(),
            "avatar" to avatar, "createdAt" to FieldValue.serverTimestamp()
        )).await()
        client   = Client(id = uid, name = name.trim(), phone = phone.trim(), email = email.trim(), avatar = avatar)
        loggedIn = true
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val uid = auth.signInWithEmailAndPassword(email.trim(), password).await().user?.uid ?: error("No user ID")
        loadClientFromFirestore(uid)
        loggedIn = true
    }

    suspend fun loadCurrentUser(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return runCatching { loadClientFromFirestore(uid); loggedIn = true }.isSuccess
    }

    private suspend fun loadClientFromFirestore(uid: String) {
        val doc = db.collection("clients").document(uid).get().await()
        client  = Client(
            id     = uid,
            name   = doc.getString("name")   ?: "",
            phone  = doc.getString("phone")  ?: "",
            email  = doc.getString("email")  ?: "",
            avatar = doc.getString("avatar") ?: ""
        )
    }

    // ── Create request ────────────────────────────────────────────────────────
    suspend fun createRequest(
        serviceType: String, description: String, address: String,
        minRating: Double = 0.0, maxPrice: Double = 0.0,
        problemType: String = "normal",
        lat: Double = 0.0, lng: Double = 0.0,
        area: String = "",
        minSkillLevel: String = ""
    ): Result<String> = runCatching {
        val c   = client ?: error("Not logged in")
        val rid = UUID.randomUUID().toString()

        db.collection("requests").document(rid).set(hashMapOf<String, Any?>(
            "clientId"        to c.id,
            "clientName"      to c.name,
            "clientPhone"     to c.phone,
            "serviceType"     to serviceType,
            "description"     to description,
            "address"         to address,
            "area"            to area,
            "lat"             to lat,
            "lng"             to lng,
            "status"          to "pending",
            "minRating"       to minRating,
            "maxPrice"        to maxPrice,
            "problemType"     to problemType,
            "minSkillLevel"   to minSkillLevel,
            "providerId"      to "",
            "providerName"    to "",
            "providerPhone"   to "",
            "providerRating"  to 0.0,
            "providerBaseFee" to 0.0,
            "rating"          to 0,
            "reviewComment"   to "",
            "createdAt"       to FieldValue.serverTimestamp()
        )).await()
        rid
    }

    // ── Client decides ────────────────────────────────────────────────────────
    suspend fun agreeToProvider(requestId: String): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(
            mapOf("status" to "accepted", "agreedAt" to FieldValue.serverTimestamp())
        ).await()
    }

    suspend fun disagreeWithProvider(requestId: String): Result<Unit> = runCatching {
        // Reset to pending so other providers can see and accept the job
        db.collection("requests").document(requestId).update(mapOf(
            "status"          to "pending",
            "providerId"      to "",
            "providerName"    to "",
            "providerPhone"   to "",
            "providerRating"  to 0.0,
            "providerBaseFee" to 0.0,
            "agreedPrice"     to 0.0
        )).await()
    }

    suspend fun cancelRequest(requestId: String): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).delete().await()
    }

    // ── Edit a pending request ────────────────────────────────────────────────
    suspend fun updateRequest(
        requestId: String, serviceType: String, description: String,
        address: String, area: String
    ): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(mapOf(
            "serviceType" to serviceType,
            "description" to description,
            "address"     to address,
            "area"        to area
        )).await()
    }

    // ── Mark job done without rating (client confirms work is finished) ────────
    suspend fun completeJob(requestId: String): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(mapOf(
            "status"      to "finished",
            "completedAt" to FieldValue.serverTimestamp()
        )).await()
    }

    // ── Submit rating after job is finished ───────────────────────────────────
    suspend fun completeAndRate(requestId: String, rating: Int, serviceRating: Int = 0, comment: String = ""): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(mapOf(
            "status"        to "finished",
            "rating"        to rating,
            "serviceRating" to serviceRating,
            "reviewComment" to comment,
            "completedAt"   to FieldValue.serverTimestamp()
        )).await()

        if (rating > 0 || serviceRating > 0) {
            val req = requests.find { it.id == requestId }
            if (req != null && req.providerId.isNotBlank()) {
                // Save review with both rating dimensions
                val reviewId = UUID.randomUUID().toString()
                db.collection("reviews").document(reviewId).set(hashMapOf<String, Any?>(
                    "providerId"    to req.providerId,
                    "clientId"      to (client?.id ?: ""),
                    "clientName"    to (client?.name ?: ""),
                    "requestId"     to requestId,
                    "serviceType"   to req.serviceType,
                    "rating"        to rating,
                    "serviceRating" to serviceRating,
                    "comment"       to comment,
                    "createdAt"     to FieldValue.serverTimestamp()
                )).await()

                // Recalculate provider's average rating using combined behavior + service score
                val allReviews = db.collection("reviews")
                    .whereEqualTo("providerId", req.providerId)
                    .get().await()
                val combinedRatings = allReviews.documents.mapNotNull { doc ->
                    val b = (doc.getLong("rating")        ?: 0).toInt()
                    val s = (doc.getLong("serviceRating") ?: 0).toInt()
                    when {
                        b > 0 && s > 0 -> (b + s) / 2.0
                        b > 0          -> b.toDouble()
                        s > 0          -> s.toDouble()
                        else           -> null
                    }
                }
                if (combinedRatings.isNotEmpty()) {
                    val avg         = combinedRatings.average()
                    val bonusPoints = when (rating) { 5 -> 100; 4 -> 30; else -> 0 }
                    val provDoc     = db.collection("providers").document(req.providerId).get().await()
                    val curPoints   = (provDoc.getLong("points") ?: 500).toInt()
                    db.collection("providers").document(req.providerId).update(mapOf(
                        "rating"        to avg,
                        "completedJobs" to combinedRatings.size,
                        "points"        to curPoints + bonusPoints
                    )).await()
                }
            }
        }
    }

    // ── Real-time listener ────────────────────────────────────────────────────
    private val notifiedRequests = mutableSetOf<String>()

    fun listenToRequests(): ListenerRegistration? {
        val uid = client?.id ?: return null
        return db.collection("requests").whereEqualTo("clientId", uid)
            .addSnapshotListener { snaps, _ ->
                requests.clear()
                snaps?.documents?.forEach { doc ->
                    // Skip soft-deleted records
                    if (doc.getBoolean("clientDeleted") == true) return@forEach

                    val status = doc.getString("status") ?: "pending"
                    val rid    = doc.id

                    if (status == "awaiting_approval" && rid !in notifiedRequests) {
                        notifiedRequests.add(rid)
                        NotificationHelper.showProviderFoundNotification(
                            requestId    = rid,
                            providerName = doc.getString("providerName") ?: "মিস্ত্রি",
                            serviceType  = AppStrings.serviceTypeName(doc.getString("serviceType") ?: ""),
                            baseFee      = doc.getDouble("providerBaseFee") ?: 0.0,
                            rating       = doc.getDouble("providerRating")  ?: 0.0
                        )
                    }
                    if (status == "no_provider" && rid !in notifiedRequests) {
                        notifiedRequests.add(rid)
                        NotificationHelper.showRequestCancelledNotification()
                    }

                    requests.add(ServiceRequest(
                        id              = doc.id,
                        clientId        = doc.getString("clientId")       ?: "",
                        clientName      = doc.getString("clientName")     ?: "",
                        clientPhone     = doc.getString("clientPhone")    ?: "",
                        serviceType     = doc.getString("serviceType")    ?: "",
                        description     = doc.getString("description")    ?: "",
                        address         = doc.getString("address")        ?: "",
                        status          = doc.getString("status")         ?: "pending",
                        minRating       = doc.getDouble("minRating")      ?: 0.0,
                        maxPrice        = doc.getDouble("maxPrice")       ?: 0.0,
                        providerId      = doc.getString("providerId")     ?: "",
                        providerName    = doc.getString("providerName")   ?: "",
                        providerPhone   = doc.getString("providerPhone")  ?: "",
                        providerRating  = doc.getDouble("providerRating") ?: 0.0,
                        providerBaseFee = doc.getDouble("providerBaseFee") ?: 0.0,
                        rating          = (doc.getLong("rating")        ?: 0).toInt(),
                        serviceRating   = (doc.getLong("serviceRating")  ?: 0).toInt(),
                        reviewComment   = doc.getString("reviewComment")  ?: "",
                        problemType     = doc.getString("problemType")    ?: "normal",
                        lat             = doc.getDouble("lat")            ?: 0.0,
                        lng             = doc.getDouble("lng")            ?: 0.0,
                        agreedPrice     = doc.getDouble("agreedPrice")    ?: 0.0,
                        minSkillLevel   = doc.getString("minSkillLevel")  ?: "",
                        createdAt       = run {
                            val ts = doc.getTimestamp("createdAt")
                            ts?.let {
                                val sdf = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.ENGLISH)
                                sdf.format(java.util.Date(it.seconds * 1000))
                            } ?: ""
                        }
                    ))
                }
            }
    }

    // ── Delete history (completed + cancelled requests) ───────────────────────
    // Soft delete — marks clientDeleted=true, does NOT delete the document
    suspend fun clearHistory(): Result<Unit> = runCatching {
        val uid = client?.id ?: error("Not logged in")
        val snaps = db.collection("requests")
            .whereEqualTo("clientId", uid)
            .whereIn("status", listOf("finished", "completed", "cancelled", "cancelled_by_client", "cancelled_by_provider", "no_provider"))
            .get().await()
        val batch = db.batch()
        snaps.documents.forEach { batch.update(it.reference, "clientDeleted", true) }
        batch.commit().await()
    }

    fun logout() {
        auth.signOut()
        client   = null
        loggedIn = false
        requests.clear()
    }
}
