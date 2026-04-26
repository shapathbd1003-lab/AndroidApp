package com.example.serviceapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceapp.data.model.Job
import com.example.serviceapp.data.repository.FakeRepository
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val provider       get() = FakeRepository.provider
    val jobs           get() = FakeRepository.jobs
    val totalGenerated get() = FakeRepository.totalGenerated
    val fastMode       get() = FakeRepository.fastMode
    val loggedIn       get() = FakeRepository.loggedIn
    val serviceTypes   get() = FakeRepository.serviceTypes

    var loginLoading   by mutableStateOf(false); private set
    var loginError     by mutableStateOf(""); private set
    var registerLoading by mutableStateOf(false); private set
    var registerError  by mutableStateOf(""); private set
    var sessionLoading by mutableStateOf(true);  private set

    // ── Auth ─────────────────────────────────────────────────────────────────

    fun loginAsync(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            loginLoading = true
            loginError   = ""
            FakeRepository.login(email, password).fold(
                onSuccess = { loginLoading = false; onSuccess() },
                onFailure = { loginLoading = false; loginError = mapError(it) }
            )
        }
    }

    fun registerAsync(
        name: String, phone: String, email: String, password: String,
        nid: String, photo: String, baseFee: Double,
        serviceType: String, certificate: String,
        skillLevel: String = "general",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            registerLoading = true
            registerError   = ""
            FakeRepository.register(name, phone, email, password, nid, photo, baseFee, serviceType, certificate, skillLevel).fold(
                onSuccess = { registerLoading = false; onSuccess() },
                onFailure = { registerLoading = false; registerError = mapError(it) }
            )
        }
    }

    fun loadCurrentSession(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val loaded = FakeRepository.loadCurrentUser()
            sessionLoading = false
            onDone(loaded)
        }
    }

    fun clearLoginError()    { loginError    = "" }
    fun clearRegisterError() { registerError = "" }

    val isApproved get() = FakeRepository.provider?.isApproved

    // ── Approval listener ─────────────────────────────────────────────────────
    private var approvalListener: ListenerRegistration? = null

    fun startApprovalListener(onApproved: () -> Unit, onRejected: () -> Unit) {
        approvalListener?.remove()
        approvalListener = FakeRepository.listenForApproval(onApproved, onRejected)
    }

    fun stopApprovalListener() {
        approvalListener?.remove()
        approvalListener = null
    }

    override fun onCleared() {
        super.onCleared()
        approvalListener?.remove()
    }

    fun logout() = FakeRepository.logout()

    // ── Simulation ───────────────────────────────────────────────────────────

    fun accept(job: Job)             = FakeRepository.accept(job)
    fun spawnJob()                   = FakeRepository.spawnJob()
    fun applySpeed(fast: Boolean)    = FakeRepository.applySpeed(fast)
    fun setAvailability(s: String)   = FakeRepository.setAvailability(s)

    // ── Language ─────────────────────────────────────────────────────────────

    fun toggleLanguage() = AppStrings.toggle()
    fun currentLang()    = AppStrings.lang

    // ── Error mapping ─────────────────────────────────────────────────────────

    private fun mapError(e: Throwable): String {
        val msg = e.message ?: ""
        val isBn = AppStrings.lang == AppLanguage.BN
        return when {
            "email address is already in use" in msg ->
                if (isBn) "এই ইমেইল ইতিমধ্যে ব্যবহৃত হচ্ছে।" else "This email is already in use."
            "INVALID_LOGIN_CREDENTIALS" in msg || "password is invalid" in msg || "invalid-credential" in msg ->
                if (isBn) "ভুল ইমেইল বা পাসওয়ার্ড।" else "Invalid email or password."
            "no user record" in msg || "user-not-found" in msg ->
                if (isBn) "এই ইমেইলে কোনো অ্যাকাউন্ট নেই।" else "No account found with this email."
            "network" in msg.lowercase() ->
                if (isBn) "নেটওয়ার্ক সমস্যা। আবার চেষ্টা করুন।" else "Network error. Try again."
            "weak-password" in msg ->
                if (isBn) "পাসওয়ার্ড কমপক্ষে ৬ অক্ষর হতে হবে।" else "Password must be at least 6 characters."
            else ->
                if (isBn) "ত্রুটি হয়েছে। আবার চেষ্টা করুন।" else "An error occurred. Try again."
        }
    }
}
