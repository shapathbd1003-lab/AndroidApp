package com.example.serviceapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentAddress(context: Context): String? =
        suspendCancellableCoroutine { cont ->
            val client = LocationServices.getFusedLocationProviderClient(context)
            val cts    = CancellationTokenSource()

            cont.invokeOnCancellation { cts.cancel() }

            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    if (location == null) { cont.resume(null); return@addOnSuccessListener }
                    reverseGeocode(context, location.latitude, location.longitude) { address ->
                        cont.resume(address)
                    }
                }
                .addOnFailureListener { cont.resume(null) }
        }

    private fun reverseGeocode(
        context: Context, lat: Double, lng: Double,
        onResult: (String?) -> Unit
    ) {
        val geocoder = Geocoder(context, Locale("bn", "BD"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lng, 1) { addresses ->
                onResult(formatAddress(addresses.firstOrNull()))
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = runCatching { geocoder.getFromLocation(lat, lng, 1) }.getOrNull()
            onResult(formatAddress(addresses?.firstOrNull()))
        }
    }

    private fun formatAddress(addr: android.location.Address?): String? {
        if (addr == null) return null
        return buildString {
            addr.subThoroughfare?.let { append(it); append(", ") }
            addr.thoroughfare?.let  { append(it); append(", ") }
            addr.subLocality?.let   { append(it); append(", ") }
            addr.locality?.let      { append(it) }
        }.trimEnd(',', ' ').ifBlank { null }
    }
}
