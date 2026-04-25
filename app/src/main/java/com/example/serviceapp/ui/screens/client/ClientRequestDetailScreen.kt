package com.example.serviceapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.data.model.ServiceRequest
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.viewmodel.ClientViewModel

@Composable
fun ClientRequestDetailScreen(requestId: String, vm: ClientViewModel, nav: NavController) {
    val request = vm.requests.find { it.id == requestId }
    val purple  = Color(0xFF6A1B9A)
    var selectedRating by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize().background(Color(0xFFF3E5F5))) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF9C27B0))))
                .statusBarsPadding()
                .padding(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Text("অনুরোধের বিবরণ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        if (request == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("অনুরোধ পাওয়া যায়নি", color = Color(0xFF757575))
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Status card
            StatusCard(request)

            // Request info
            InfoCard(request)

            // Provider card (if accepted)
            if (request.status == "accepted" || request.status == "completed") {
                ProviderCard(request)
            }

            // Rating card (if completed and not yet rated)
            if (request.status == "completed" && request.rating == 0) {
                RatingCard(selectedRating) { selectedRating = it }
            }

            // Completed with rating
            if (request.status == "completed" && request.rating > 0) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text("আপনার রেটিং: ", fontSize = 15.sp, color = Color(0xFF2E7D32))
                        Text("⭐".repeat(request.rating), fontSize = 18.sp)
                    }
                }
            }
        }

        // Action buttons
        Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
            when (request.status) {
                "accepted" -> Button(
                    onClick = {
                        vm.completeAndRate(request.id, 0) { /* dashboard will refresh */ }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("✅ কাজ সম্পন্ন হয়েছে", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                "completed" -> if (request.rating == 0) {
                    Button(
                        onClick = {
                            if (selectedRating > 0) {
                                vm.completeAndRate(request.id, selectedRating) {
                                    nav.popBackStack()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = selectedRating > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = purple, disabledContainerColor = Color(0xFFBDBDBD))
                    ) {
                        Text("রেটিং জমা দিন ⭐", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = { nav.navigate(Screen.ClientDashboard.route) { popUpTo(0) { inclusive = true } } },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("ড্যাশবোর্ডে ফিরুন", fontSize = 15.sp, color = purple)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCard(req: ServiceRequest) {
    val (bg, fg, emoji, label) = when (req.status) {
        "accepted"  -> listOf(Color(0xFFE3F2FD), Color(0xFF1565C0), "🔧", "মিস্ত্রি নিশ্চিত করেছেন!")
        "completed" -> listOf(Color(0xFFE8F5E9), Color(0xFF2E7D32), "☑️", "কাজ সম্পন্ন!")
        else        -> listOf(Color(0xFFFFF3E0), Color(0xFFE65100), "⏳", "অপেক্ষমান — মিস্ত্রি খোঁজা হচ্ছে...")
    }

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = bg as Color)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(48.dp).background(Color.White.copy(alpha = 0.6f), CircleShape), contentAlignment = Alignment.Center) {
                Text(emoji as String, fontSize = 22.sp)
            }
            Column {
                Text(label as String, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = fg as Color)
                if (req.status == "pending") {
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp), color = fg, trackColor = fg.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
private fun InfoCard(req: ServiceRequest) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("অনুরোধের তথ্য", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
            HorizontalDivider()
            InfoRow("🔧 সেবা", req.serviceType)
            InfoRow("📝 বিবরণ", req.description)
            InfoRow("📍 ঠিকানা", req.address)
        }
    }
}

@Composable
private fun ProviderCard(req: ServiceRequest) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("মিস্ত্রির তথ্য", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
            HorizontalDivider(color = Color(0xFF6A1B9A).copy(alpha = 0.2f))
            InfoRow("👤 নাম",  req.providerName)
            InfoRow("📞 ফোন", req.providerPhone)
        }
    }
}

@Composable
private fun RatingCard(selected: Int, onSelect: (Int) -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("মিস্ত্রিকে রেটিং দিন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { star ->
                    IconButton(onClick = { onSelect(star) }) {
                        Icon(
                            if (star <= selected) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (star <= selected) Color(0xFFFFA000) else Color(0xFFBDBDBD),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
            if (selected > 0) {
                Spacer(Modifier.height(4.dp))
                val label = when (selected) { 1 -> "খুব খারাপ" 2 -> "খারাপ" 3 -> "ঠিকঠাক" 4 -> "ভালো" else -> "চমৎকার!" }
                Text(label, fontSize = 13.sp, color = Color(0xFFFFA000), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 13.sp, color = Color(0xFF757575), modifier = Modifier.widthIn(min = 80.dp))
        Text(value, fontSize = 13.sp, color = Color(0xFF212121), fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
    }
}
