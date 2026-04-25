package com.example.serviceapp.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.serviceapp.data.model.Client
import com.example.serviceapp.data.model.ServiceRequest
import com.example.serviceapp.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

object ClientRepository {

    var client  by mutableStateOf<Client?>(null)
    val requests = mutableStateListOf<ServiceRequest>()
    var loggedIn by mutableStateOf(false)

    private val auth: FirebaseAuth      get() = FirebaseAuth.getInstance()
    private val db:   FirebaseFirestore get() = FirebaseFirestore.getInstance()

    private data class FakeProvider(
        val name: String, val phone: String, val id: String,
        val rating: Double, val baseFee: Double
    )

    private val fakeProviders = listOf(
        FakeProvider("রাকিব হোসেন",  "01712345678", "p_001", 4.5, 500.0),
        FakeProvider("মো. করিম",     "01812345678", "p_002", 4.2, 350.0),
        FakeProvider("ফারহান আহমেদ", "01912345678", "p_003", 4.8, 400.0),
        FakeProvider("তানভীর ইসলাম", "01512345678", "p_005", 3.9, 450.0),
        FakeProvider("সুমাইয়া বেগম", "01612345678", "p_004", 4.0, 600.0),
        FakeProvider("নুসরাত জাহান", "01312345678", "p_006", 4.6, 550.0),
    )

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
        minRating: Double = 0.0, maxPrice: Double = 0.0
    ): Result<String> = runCatching {
        val c   = client ?: error("Not logged in")
        val rid = UUID.randomUUID().toString()

        db.collection("requests").document(rid).set(hashMapOf<String, Any?>(
            "clientId"       to c.id,
            "clientName"     to c.name,
            "clientPhone"    to c.phone,
            "serviceType"    to serviceType,
            "description"    to description,
            "address"        to address,
            "status"         to "pending",
            "minRating"      to minRating,
            "maxPrice"       to maxPrice,
            "providerId"     to "",
            "providerName"   to "",
            "providerPhone"  to "",
            "providerRating" to 0.0,
            "providerBaseFee" to 0.0,
            "rating"         to 0,
            "reviewComment"  to "",
            "createdAt"      to FieldValue.serverTimestamp()
        )).await()

        // Fake provider matches after 15–25 seconds
        CoroutineScope(Dispatchers.IO).launch {
            delay((15_000L..25_000L).random())

            val eligible = fakeProviders.filter { p ->
                (minRating == 0.0 || p.rating >= minRating) &&
                (maxPrice  == 0.0 || p.baseFee <= maxPrice)
            }

            if (eligible.isEmpty()) {
                db.collection("requests").document(rid).update(mapOf("status" to "no_provider")).await()
                return@launch
            }

            db.collection("requests").document(rid).update(mapOf(
                "status"          to "awaiting_approval",
                "providerId"      to eligible.random().id,
                "providerName"    to eligible.random().name,
                "providerPhone"   to eligible.random().phone,
                "providerRating"  to eligible.random().rating,
                "providerBaseFee" to eligible.random().baseFee,
                "matchedAt"       to FieldValue.serverTimestamp()
            )).await()
        }
        rid
    }

    // ── Client decides ────────────────────────────────────────────────────────
    suspend fun agreeToProvider(requestId: String): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(
            mapOf("status" to "accepted", "agreedAt" to FieldValue.serverTimestamp())
        ).await()
    }

    suspend fun disagreeWithProvider(requestId: String): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(
            mapOf("status" to "cancelled", "cancelledAt" to FieldValue.serverTimestamp())
        ).await()
    }

    suspend fun cancelRequest(requestId: String): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(
            mapOf("status" to "cancelled", "cancelledAt" to FieldValue.serverTimestamp())
        ).await()
    }

    // ── Complete + review ─────────────────────────────────────────────────────
    suspend fun completeAndRate(requestId: String, rating: Int, comment: String = ""): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(mapOf(
            "status"        to "completed",
            "rating"        to rating,
            "reviewComment" to comment,
            "completedAt"   to FieldValue.serverTimestamp()
        )).await()

        // Store review in separate collection for provider profile
        if (rating > 0) {
            val req = requests.find { it.id == requestId }
            if (req != null && req.providerId.isNotBlank()) {
                val reviewId = UUID.randomUUID().toString()
                db.collection("reviews").document(reviewId).set(hashMapOf<String, Any?>(
                    "providerId"    to req.providerId,
                    "clientId"      to (client?.id ?: ""),
                    "clientName"    to (client?.name ?: ""),
                    "requestId"     to requestId,
                    "serviceType"   to req.serviceType,
                    "rating"        to rating,
                    "comment"       to comment,
                    "createdAt"     to FieldValue.serverTimestamp()
                )).await()
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
                    val status = doc.getString("status") ?: "pending"
                    val rid    = doc.id

                    // Fire notification once when provider is matched
                    if (status == "awaiting_approval" && rid !in notifiedRequests) {
                        notifiedRequests.add(rid)
                        NotificationHelper.showProviderFoundNotification(
                            providerName = doc.getString("providerName") ?: "মিস্ত্রি",
                            serviceType  = doc.getString("serviceType")  ?: "",
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
                        rating          = (doc.getLong("rating")  ?: 0).toInt(),
                        reviewComment   = doc.getString("reviewComment")  ?: ""
                    ))
                }
            }
    }

    // ── Delete history (completed + cancelled requests) ───────────────────────
    suspend fun clearHistory(): Result<Unit> = runCatching {
        val uid = client?.id ?: error("Not logged in")
        val snaps = db.collection("requests")
            .whereEqualTo("clientId", uid)
            .whereIn("status", listOf("completed", "cancelled", "no_provider"))
            .get().await()
        val batch = db.batch()
        snaps.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    fun logout() {
        auth.signOut()
        client   = null
        loggedIn = false
        requests.clear()
    }
}
