package com.example.serviceapp.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.serviceapp.data.model.Client
import com.example.serviceapp.data.model.ServiceRequest
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

    var client   by mutableStateOf<Client?>(null)
    val requests  = mutableStateListOf<ServiceRequest>()
    var loggedIn  by mutableStateOf(false)

    private val auth: FirebaseAuth      get() = FirebaseAuth.getInstance()
    private val db:   FirebaseFirestore get() = FirebaseFirestore.getInstance()

    private val fakeProviders = listOf(
        Triple("রাকিব হোসেন",  "01712345678", "provider_001"),
        Triple("মো. করিম",     "01812345678", "provider_002"),
        Triple("ফারহান আহমেদ", "01912345678", "provider_003"),
        Triple("তানভীর ইসলাম", "01512345678", "provider_005")
    )

    // ── Register ─────────────────────────────────────────────────────────────
    suspend fun register(
        name: String, phone: String, email: String, password: String,
        avatar: String = ""
    ): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid    = result.user?.uid ?: error("No user ID")

        db.collection("clients").document(uid).set(
            hashMapOf<String, Any?>(
                "name"      to name.trim(),
                "phone"     to phone.trim(),
                "email"     to email.trim(),
                "avatar"    to avatar,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()

        client   = Client(id = uid, name = name.trim(), phone = phone.trim(), email = email.trim(), avatar = avatar)
        loggedIn = true
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val uid    = result.user?.uid ?: error("No user ID")
        loadClientFromFirestore(uid)
        loggedIn = true
    }

    // ── Auto-restore session ──────────────────────────────────────────────────
    suspend fun loadCurrentUser(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return runCatching {
            loadClientFromFirestore(uid)
            loggedIn = true
        }.isSuccess
    }

    private suspend fun loadClientFromFirestore(uid: String) {
        val doc = db.collection("clients").document(uid).get().await()
        client = Client(
            id     = uid,
            name   = doc.getString("name")   ?: "",
            phone  = doc.getString("phone")  ?: "",
            email  = doc.getString("email")  ?: "",
            avatar = doc.getString("avatar") ?: ""
        )
    }

    // ── Create request ────────────────────────────────────────────────────────
    suspend fun createRequest(
        serviceType: String, description: String, address: String
    ): Result<String> = runCatching {
        val c   = client ?: error("Not logged in")
        val rid = UUID.randomUUID().toString()

        val data = hashMapOf<String, Any?>(
            "clientId"    to c.id,
            "clientName"  to c.name,
            "clientPhone" to c.phone,
            "serviceType" to serviceType,
            "description" to description,
            "address"     to address,
            "status"      to "pending",
            "providerId"  to "",
            "providerName"  to "",
            "providerPhone" to "",
            "rating"      to 0,
            "createdAt"   to FieldValue.serverTimestamp()
        )
        db.collection("requests").document(rid).set(data).await()

        // Fake provider accepts after 5 seconds
        CoroutineScope(Dispatchers.IO).launch {
            delay(5000)
            val fake = fakeProviders.random()
            db.collection("requests").document(rid).update(
                mapOf(
                    "status"        to "accepted",
                    "providerId"    to fake.third,
                    "providerName"  to fake.first,
                    "providerPhone" to fake.second,
                    "acceptedAt"    to FieldValue.serverTimestamp()
                )
            ).await()
        }

        rid
    }

    // ── Mark completed + save rating ──────────────────────────────────────────
    suspend fun completeAndRate(requestId: String, rating: Int): Result<Unit> = runCatching {
        db.collection("requests").document(requestId).update(
            mapOf(
                "status"      to "completed",
                "rating"      to rating,
                "completedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    // ── Real-time listener for client's requests ───────────────────────────────
    fun listenToRequests(): ListenerRegistration? {
        val uid = client?.id ?: return null
        return db.collection("requests")
            .whereEqualTo("clientId", uid)
            .addSnapshotListener { snaps, _ ->
                requests.clear()
                snaps?.documents?.forEach { doc ->
                    requests.add(ServiceRequest(
                        id            = doc.id,
                        clientId      = doc.getString("clientId")      ?: "",
                        clientName    = doc.getString("clientName")    ?: "",
                        clientPhone   = doc.getString("clientPhone")   ?: "",
                        serviceType   = doc.getString("serviceType")   ?: "",
                        description   = doc.getString("description")   ?: "",
                        address       = doc.getString("address")       ?: "",
                        status        = doc.getString("status")        ?: "pending",
                        providerId    = doc.getString("providerId")    ?: "",
                        providerName  = doc.getString("providerName")  ?: "",
                        providerPhone = doc.getString("providerPhone") ?: "",
                        rating        = (doc.getLong("rating") ?: 0).toInt()
                    ))
                }
            }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    fun logout() {
        auth.signOut()
        client   = null
        loggedIn = false
        requests.clear()
    }
}
