package com.example.serviceapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.data.repository.ClientRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {

    val client   get() = ClientRepository.client
    val requests get() = ClientRepository.requests
    val loggedIn get() = ClientRepository.loggedIn

    var loginLoading   by mutableStateOf(false); private set
    var loginError     by mutableStateOf("");     private set
    var registerLoading by mutableStateOf(false); private set
    var registerError  by mutableStateOf("");     private set
    var requestLoading by mutableStateOf(false);  private set
    var sessionLoading by mutableStateOf(true);   private set

    private var requestsListener: ListenerRegistration? = null

    // ── Auth ──────────────────────────────────────────────────────────────────

    fun registerAsync(
        name: String, phone: String, email: String, password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            registerLoading = true
            registerError   = ""
            ClientRepository.register(name, phone, email, password).fold(
                onSuccess = { registerLoading = false; startListening(); onSuccess() },
                onFailure = { registerLoading = false; registerError = mapError(it) }
            )
        }
    }

    fun loginAsync(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            loginLoading = true
            loginError   = ""
            ClientRepository.login(email, password).fold(
                onSuccess = { loginLoading = false; startListening(); onSuccess() },
                onFailure = { loginLoading = false; loginError = mapError(it) }
            )
        }
    }

    fun loadCurrentSession(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val loaded = ClientRepository.loadCurrentUser()
            sessionLoading = false
            if (loaded) startListening()
            onDone(loaded)
        }
    }

    // ── Requests ──────────────────────────────────────────────────────────────

    fun createRequest(
        serviceType: String, description: String, address: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            requestLoading = true
            ClientRepository.createRequest(serviceType, description, address).fold(
                onSuccess = { requestLoading = false; onSuccess() },
                onFailure = { requestLoading = false }
            )
        }
    }

    fun completeAndRate(requestId: String, rating: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            ClientRepository.completeAndRate(requestId, rating)
            onDone()
        }
    }

    // ── Listener ──────────────────────────────────────────────────────────────

    private fun startListening() {
        requestsListener?.remove()
        requestsListener = ClientRepository.listenToRequests()
    }

    fun logout() {
        requestsListener?.remove()
        requestsListener = null
        ClientRepository.logout()
    }

    override fun onCleared() {
        super.onCleared()
        requestsListener?.remove()
    }

    fun clearErrors() { loginError = ""; registerError = "" }

    private fun mapError(e: Throwable): String {
        val msg = e.message ?: ""
        return when {
            "email address is already in use" in msg -> "এই ইমেইল ইতিমধ্যে ব্যবহৃত।"
            "INVALID_LOGIN_CREDENTIALS" in msg || "invalid-credential" in msg -> "ভুল ইমেইল বা পাসওয়ার্ড।"
            "no user record" in msg -> "এই ইমেইলে কোনো অ্যাকাউন্ট নেই।"
            "network" in msg.lowercase() -> "নেটওয়ার্ক সমস্যা। আবার চেষ্টা করুন।"
            "weak-password" in msg -> "পাসওয়ার্ড কমপক্ষে ৬ অক্ষর হতে হবে।"
            else -> "ত্রুটি হয়েছে। আবার চেষ্টা করুন।"
        }
    }
}
