package com.example.serviceapp.ui.components

import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun EmbeddedMap(address: String, height: Dp = 200.dp) {
    val encoded = Uri.encode(address)
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled   = true
                    loadWithOverviewMode = true
                    useWideViewPort      = true
                    @Suppress("DEPRECATION")
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode           = WebSettings.LOAD_DEFAULT
                }
                loadUrl("https://maps.google.com/maps?q=$encoded&output=embed&z=15")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
fun OpenMapsButton(address: String, color: Color = Color(0xFF1A237E)) {
    val ctx = LocalContext.current
    Button(
        onClick = {
            val uri    = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            // Fallback to browser if Maps app not installed
            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(address)}"))
            runCatching { ctx.startActivity(intent) }
                .onFailure   { ctx.startActivity(fallback) }
        },
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(10.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(Icons.Default.Map, null)
        Text("  Google Maps এ খুলুন", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
