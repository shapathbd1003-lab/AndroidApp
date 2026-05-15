package com.example.serviceapp.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Reactive singleton so Compose screens can observe when a notification
 * sets a pending request ID and navigate immediately, even when the app
 * is already running and MainActivity.onNewIntent fires.
 */
object PendingNavigation {
    var clientRequestId: String? by mutableStateOf(null)
    var providerJobId:   String? by mutableStateOf(null)
}
