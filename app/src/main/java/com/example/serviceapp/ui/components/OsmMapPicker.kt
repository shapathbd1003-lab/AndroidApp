package com.example.serviceapp.ui.components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.serviceapp.utils.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OsmMapPickerDialog(
    onDismiss:  () -> Unit,
    onSelected: (address: String, lat: Double, lng: Double) -> Unit
) {
    var selectedLat     by remember { mutableStateOf(0.0) }
    var selectedLng     by remember { mutableStateOf(0.0) }
    var selectedAddress by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }
    val context         = LocalContext.current
    val scope           = rememberCoroutineScope()

    // JavaScript bridge
    class LocationBridge {
        @JavascriptInterface
        fun onTap(lat: Double, lng: Double) {
            selectedLat = lat
            selectedLng = lng
            isLoading   = true
            scope.launch {
                val addr = withContext(Dispatchers.IO) {
                    runCatching { LocationHelper.getCurrentAddress(context) }.getOrNull()
                }
                // Use Nominatim reverse geocoding
                val nominatimAddr = runCatching {
                    val url  = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lng&accept-language=bn"
                    val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                    conn.setRequestProperty("User-Agent", "MistriChai-App")
                    val json = conn.inputStream.bufferedReader().readText()
                    val display = json.substringAfter("\"display_name\":\"").substringBefore("\"")
                    // Take first 3 parts of the address
                    display.split(",").take(3).joinToString(", ").trim()
                }.getOrNull() ?: addr ?: "$lat, $lng"
                selectedAddress = nominatimAddr
                isLoading       = false
            }
        }
    }

    val mapHtml = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
          <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
          <style>
            html,body,#map{height:100%;margin:0;padding:0;}
            #hint{position:absolute;top:10px;left:50%;transform:translateX(-50%);
                  background:rgba(0,0,0,0.65);color:#fff;padding:6px 14px;
                  border-radius:20px;font-size:13px;z-index:999;pointer-events:none;}
          </style>
        </head>
        <body>
          <div id="hint">ট্যাপ করুন অবস্থান বেছে নিতে</div>
          <div id="map"></div>
          <script>
            var map = L.map('map').setView([23.8103, 90.4125], 13);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
              {attribution:'© OpenStreetMap'}).addTo(map);
            var marker = null;
            map.on('click', function(e){
              if(marker) marker.remove();
              marker = L.marker(e.latlng).addTo(map);
              document.getElementById('hint').style.display='none';
              Android.onTap(e.latlng.lat, e.latlng.lng);
            });
          </script>
        </body>
        </html>
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            shape    = RoundedCornerShape(16.dp),
            color    = Color.White
        ) {
            Column {
                // Header
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("📍 ম্যাপ থেকে বেছে নিন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                }

                // Map
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled  = true
                            webViewClient = WebViewClient()
                            addJavascriptInterface(LocationBridge(), "Android")
                            loadDataWithBaseURL(null, mapHtml, "text/html", "utf-8", null)
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(8.dp))
                )

                // Selected address + confirm
                Column(Modifier.padding(16.dp)) {
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text("ঠিকানা খোঁজা হচ্ছে...", fontSize = 13.sp, color = Color(0xFF757575))
                        }
                    } else if (selectedAddress.isNotBlank()) {
                        Text("📍 $selectedAddress", fontSize = 13.sp, color = Color(0xFF212121))
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { onSelected(selectedAddress, selectedLat, selectedLng) },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                        ) {
                            Text("এই অবস্থান নির্বাচন করুন ✓", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("ম্যাপে ট্যাপ করুন অবস্থান বেছে নিতে", fontSize = 13.sp, color = Color(0xFF9E9E9E))
                    }
                }
            }
        }
    }
}
