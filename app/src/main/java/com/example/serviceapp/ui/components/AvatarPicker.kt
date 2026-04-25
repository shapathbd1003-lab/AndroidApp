package com.example.serviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

val presetAvatars = listOf(
    "https://i.pravatar.cc/150?img=1",
    "https://i.pravatar.cc/150?img=3",
    "https://i.pravatar.cc/150?img=5",
    "https://i.pravatar.cc/150?img=8",
    "https://i.pravatar.cc/150?img=11",
    "https://i.pravatar.cc/150?img=14",
    "https://i.pravatar.cc/150?img=20",
    "https://i.pravatar.cc/150?img=25",
    "https://i.pravatar.cc/150?img=32",
    "https://i.pravatar.cc/150?img=47",
)

@Composable
fun AvatarPicker(
    selected: String,
    onSelect: (String) -> Unit,
    onUploadFromGallery: () -> Unit = {}
) {
    val green = Color(0xFF1B5E20)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

        // Large preview with upload badge
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = selected.ifBlank { presetAvatars.first() },
                contentDescription = null,
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .border(3.dp, green, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .offset(x = (-2).dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(green)
                    .clickable { onUploadFromGallery() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        // Upload from gallery button
        OutlinedButton(
            onClick = onUploadFromGallery,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(135, 206, 255), // This sets the background
                contentColor = Color.Blue  // This sets the text/icon color
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, green.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Upload from Gallery", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(12.dp))

        // Preset row
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f).height(1.dp).background(Color(0xFFEEEEEE)))
            Text("  or choose preset  ", fontSize = 11.sp, color = Color(0xFFBDBDBD))
            Box(Modifier.weight(1f).height(1.dp).background(Color(0xFFEEEEEE)))
        }

        Spacer(Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(presetAvatars) { url ->
                val isSelected = selected == url
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) green else Color(0xFFE0E0E0),
                            shape = CircleShape
                        )
                        .clickable { onSelect(url) }
                )
            }
        }
    }
}
