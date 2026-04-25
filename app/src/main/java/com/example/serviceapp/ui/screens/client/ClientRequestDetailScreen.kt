package com.example.serviceapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    val request        = vm.requests.find { it.id == requestId }
    val purple         = Color(0xFF6A1B9A)
    var selectedRating by remember { mutableStateOf(0) }
    var reviewText     by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(Color(0xFFF3E5F5))) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF4A148C), Color(0xFF9C27B0))))
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(request)
            InfoCard(request)
            if (request.minRating > 0 || request.maxPrice > 0) FilterSummaryCard(request)
            if (request.status == "awaiting_approval") ProviderApprovalCard(request)
            else if (request.status == "accepted" || request.status == "completed") ProviderInfoCard(request)
            if (request.status == "completed" && request.rating == 0) RatingCard(selectedRating, reviewText, { selectedRating = it }, { reviewText = it })
            if (request.status == "completed" && request.rating > 0) CompletedReviewCard(request)
        }

        // Action bar
        val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = purple, focusedLabelColor = purple, cursorColor = purple
        )
        Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
            when (request.status) {
                "pending" -> OutlinedButton(
                    onClick = { vm.cancelRequest(request.id); nav.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC62828))
                ) {
                    Text("অনুরোধ বাতিল করুন", fontSize = 15.sp, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                }
                "awaiting_approval" -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { vm.disagreeWithProvider(request.id) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC62828))
                    ) {
                        Text("❌ না, ধন্যবাদ", fontSize = 14.sp, color = Color(0xFFC62828))
                    }
                    Button(
                        onClick = { vm.agreeToProvider(request.id) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("✅ রাজি আছি", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                "accepted" -> Button(
                    onClick = { vm.completeAndRate(request.id, 0) { } },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("✅ কাজ সম্পন্ন হয়েছে", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                "completed" -> if (request.rating == 0) Button(
                    onClick = {
                        if (selectedRating > 0) {
                            vm.completeAndRate(request.id, selectedRating, reviewText) { nav.popBackStack() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = selectedRating > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = purple, disabledContainerColor = Color(0xFFBDBDBD))
                ) {
                    Text("রেটিং ও রিভিউ জমা দিন ⭐", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusCard(req: ServiceRequest) {
    val (bg, fg, emoji, label) = when (req.status) {
        "awaiting_approval" -> listOf(Color(0xFFE8EAF6), Color(0xFF1A237E), "🔍", "মিস্ত্রি পাওয়া গেছে — আপনার সিদ্ধান্ত নিন")
        "accepted"          -> listOf(Color(0xFFE3F2FD), Color(0xFF1565C0), "🔧", "মিস্ত্রি নিশ্চিত — কাজ শুরু হচ্ছে!")
        "completed"         -> listOf(Color(0xFFE8F5E9), Color(0xFF2E7D32), "☑️", "কাজ সম্পন্ন!")
        "cancelled"         -> listOf(Color(0xFFFFEBEE), Color(0xFFC62828), "❌", "অনুরোধ বাতিল হয়েছে")
        "no_provider"       -> listOf(Color(0xFFFFF3E0), Color(0xFFE65100), "😔", "আপনার ফিল্টারে কোনো মিস্ত্রি পাওয়া যায়নি")
        else                -> listOf(Color(0xFFFFF8E1), Color(0xFFE65100), "⏳", "মিস্ত্রি খোঁজা হচ্ছে (১৫–২৫ সেকেন্ড)...")
    }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = bg as Color)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(48.dp).background(Color.White.copy(alpha = 0.6f), CircleShape), contentAlignment = Alignment.Center) {
                Text(emoji as String, fontSize = 22.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(label as String, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = fg as Color)
                if (req.status == "pending") {
                    Spacer(Modifier.height(6.dp))
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
private fun FilterSummaryCard(req: ServiceRequest) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("🔍 ফিল্টার:", fontSize = 12.sp, color = Color(0xFF6A1B9A), fontWeight = FontWeight.SemiBold)
            if (req.minRating > 0) Text("রেটিং ≥ ${req.minRating}", fontSize = 12.sp, color = Color(0xFF424242))
            if (req.maxPrice > 0)  Text("ফি ≤ ৳${req.maxPrice.toInt()}", fontSize = 12.sp, color = Color(0xFF424242))
        }
    }
}

@Composable
private fun ProviderApprovalCard(req: ServiceRequest) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("মিস্ত্রির প্রস্তাব", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
            HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.2f))
            InfoRow("👤 নাম",       req.providerName)
            InfoRow("📞 ফোন",       req.providerPhone)
            InfoRow("⭐ রেটিং",     "%.1f / 5.0".format(req.providerRating))
            InfoRow("💰 বেস ফি",    "৳ ${req.providerBaseFee.toInt()} প্রতি সেবায়")
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9)) {
                Text(
                    "এই মিস্ত্রিকে নিয়োগ দিতে চান? নিচে সম্মতি দিন।",
                    fontSize = 13.sp, color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun ProviderInfoCard(req: ServiceRequest) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("নিযুক্ত মিস্ত্রি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
            HorizontalDivider(color = Color(0xFF6A1B9A).copy(alpha = 0.2f))
            InfoRow("👤 নাম",    req.providerName)
            InfoRow("📞 ফোন",    req.providerPhone)
            InfoRow("⭐ রেটিং", "%.1f / 5.0".format(req.providerRating))
            InfoRow("💰 ফি",     "৳ ${req.providerBaseFee.toInt()}")
        }
    }
}

@Composable
private fun RatingCard(selected: Int, comment: String, onRate: (Int) -> Unit, onComment: (String) -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("রেটিং ও রিভিউ দিন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..5).forEach { star ->
                    IconButton(onClick = { onRate(star) }, modifier = Modifier.size(44.dp)) {
                        Icon(
                            if (star <= selected) Icons.Default.Star else Icons.Default.StarBorder,
                            null,
                            tint = if (star <= selected) Color(0xFFFFA000) else Color(0xFFBDBDBD),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
            if (selected > 0) {
                val label = when (selected) { 1 -> "খুব খারাপ 😞" 2 -> "খারাপ 😕" 3 -> "ঠিকঠাক 😐" 4 -> "ভালো 😊" else -> "চমৎকার! 🤩" }
                Text(label, fontSize = 14.sp, color = Color(0xFFFFA000), fontWeight = FontWeight.SemiBold)
            }

            OutlinedTextField(
                value = comment,
                onValueChange = onComment,
                placeholder = { Text("আপনার অভিজ্ঞতা লিখুন (ঐচ্ছিক)", color = Color(0xFFBDBDBD)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF6A1B9A), cursorColor = Color(0xFF6A1B9A)),
                maxLines = 4
            )
        }
    }
}

@Composable
private fun CompletedReviewCard(req: ServiceRequest) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("আপনার রিভিউ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Text("${"⭐".repeat(req.rating)} (${req.rating}/5)", fontSize = 16.sp)
            if (req.reviewComment.isNotBlank()) Text("\"${req.reviewComment}\"", fontSize = 13.sp, color = Color(0xFF424242))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 13.sp, color = Color(0xFF757575), modifier = Modifier.widthIn(min = 90.dp))
        Text(value, fontSize = 13.sp, color = Color(0xFF212121), fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
    }
}
