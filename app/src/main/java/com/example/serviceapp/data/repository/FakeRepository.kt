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
    private var requestsListener: ListenerRegistration? = null
    private var approvalListener: ListenerRegistration? = null

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
            isApproved   = if (approvedRaw == null) null else approvedRaw as? Boolean
        )
    }

    // ── Real-time listener: client requests matching provider's service & skill ─
    fun startListeningToRequests() {
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
        val allowed = ServiceData.allowedTypes(p.skillLevel)

        requestsListener?.remove()
        requestsListener = db.collection("requests")
            .whereEqualTo("serviceType", p.serviceType)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snaps, _ ->
                jobs.clear()
                snaps?.documents?.forEach { doc ->
                    val problemType = doc.getString("problemType") ?: "normal"
                    if (problemType !in allowed) return@forEach

                    jobs.add(Job(
                        id          = doc.id,
                        description = "${AppStrings.serviceTypeName(doc.getString("serviceType") ?: "")}",
                        address     = doc.getString("address")     ?: "",
                        phone       = doc.getString("clientPhone") ?: "",
                        overview    = doc.getString("description") ?: "",
                        problemType = problemType,
                        status      = "pending"
                    ))
                }
            }
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
    fun accept(job: Job) {
        // Update local state immediately for responsive UI
        val idx = jobs.indexOfFirst { it.id == job.id }
        if (idx >= 0) {
            jobs[idx] = job.copy(status = "done")
            provider?.let { p ->
                p.advance += p.baseFee
                p.history.add(ServiceHistory(job.id, job.description, p.baseFee))
            }
        }

        // Write acceptance to Firestore
        val p   = provider ?: return
        val uid = auth.currentUser?.uid ?: return
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
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    fun logout() {
        auth.signOut()
        requestsListener?.remove()
        requestsListener = null
        loggedIn  = false
        provider  = null
        jobs.clear()
    }

    fun setAvailability(status: String) { provider?.availability = status }

    fun clearHistory() {
        provider?.let { p ->
            p.history.clear()
            p.advance = 0.0
        }
    }
}
