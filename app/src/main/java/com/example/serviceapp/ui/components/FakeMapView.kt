package com.example.serviceapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background

@Composable
fun FakeMapView(address: String, modifier: Modifier = Modifier) {

    Box(
        modifier = modifier.clip(RoundedCornerShape(14.dp))
    ) {
        Canvas(Modifier.fillMaxSize()) {

            // Base ground
            drawRect(Color(0xFFC8E6C9))

            // Park/green block top-left
            drawRect(Color(0xFFA5D6A7), topLeft = Offset(0f, 0f), size = Size(size.width * 0.25f, size.height * 0.38f))

            // Main horizontal road
            drawRect(Color(0xFFECEFF1), topLeft = Offset(0f, size.height * 0.42f), size = Size(size.width, size.height * 0.12f))
            drawRect(Color(0xFFBDBDBD), topLeft = Offset(0f, size.height * 0.477f), size = Size(size.width, 2f))

            // Top horizontal road
            drawRect(Color(0xFFECEFF1), topLeft = Offset(0f, size.height * 0.18f), size = Size(size.width, size.height * 0.08f))

            // Main vertical road
            drawRect(Color(0xFFECEFF1), topLeft = Offset(size.width * 0.38f, 0f), size = Size(size.width * 0.11f, size.height))
            drawRect(Color(0xFFBDBDBD), topLeft = Offset(size.width * 0.432f, 0f), size = Size(2f, size.height))

            // Right vertical road
            drawRect(Color(0xFFECEFF1), topLeft = Offset(size.width * 0.72f, 0f), size = Size(size.width * 0.08f, size.height))

            // Buildings
            val building = Color(0xFFB0BEC5)
            val buildingDark = Color(0xFF90A4AE)
            drawRect(building, Offset(size.width * 0.04f, size.height * 0.28f), Size(size.width * 0.3f, size.height * 0.12f))
            drawRect(buildingDark, Offset(size.width * 0.04f, size.height * 0.28f), Size(size.width * 0.13f, size.height * 0.12f))
            drawRect(building, Offset(size.width * 0.53f, size.height * 0.28f), Size(size.width * 0.15f, size.height * 0.12f))
            drawRect(building, Offset(size.width * 0.82f, size.height * 0.06f), Size(size.width * 0.15f, size.height * 0.1f))
            drawRect(building, Offset(size.width * 0.04f, size.height * 0.58f), Size(size.width * 0.3f, size.height * 0.28f))
            drawRect(buildingDark, Offset(size.width * 0.04f, size.height * 0.58f), Size(size.width * 0.13f, size.height * 0.28f))
            drawRect(building, Offset(size.width * 0.53f, size.height * 0.58f), Size(size.width * 0.15f, size.height * 0.28f))
            drawRect(building, Offset(size.width * 0.82f, size.height * 0.58f), Size(size.width * 0.15f, size.height * 0.28f))

            val cx = size.width * 0.433f
            val cy = size.height * 0.477f

            // Pulse rings
            drawCircle(Color(0x221B5E20), radius = 60f, center = Offset(cx, cy))
            drawCircle(Color(0x441B5E20), radius = 38f, center = Offset(cx, cy))

            // Marker dot
            drawCircle(Color(0xFF1B5E20), radius = 18f, center = Offset(cx, cy))
            drawCircle(Color.White, radius = 7f, center = Offset(cx, cy))
        }

        // Address label at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.92f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, null, tint = Color(0xFF1B5E20), modifier = Modifier.width(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(address, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF212121))
        }
    }
}
