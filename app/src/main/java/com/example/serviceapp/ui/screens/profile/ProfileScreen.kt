package com.example.serviceapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore

private data class ReviewItem(
    val clientName: String,
    val serviceType: String,
    val rating: Int,
    val comment: String
)

@Composable
fun ProfileScreen(vm: MainViewModel, nav: NavController) {

    val p = vm.provider ?: return

    val reviews             = remember { mutableStateListOf<ReviewItem>() }
    var showDeleteConfirm  by remember { mutableStateOf(false) }
    LaunchedEffect(p.id) {
        FirebaseFirestore.getInstance()
            .collection("reviews")
            .whereEqualTo("providerId", p.id)
            .get()
            .addOnSuccessListener { snaps ->
                reviews.clear()
                snaps.documents.forEach { doc ->
                    reviews.add(ReviewItem(
                        clientName  = doc.getString("clientName")  ?: "গ্রাহক",
                        serviceType = doc.getString("serviceType") ?: "",
                        rating      = (doc.getLong("rating") ?: 0).toInt(),
                        comment     = doc.getString("comment")     ?: ""
                    ))
                }
            }
    }

    val availabilityOptions = listOf("available", "working", "unavailable")
    val availabilityLabels = mapOf(
        "available" to AppStrings.available,
        "working" to AppStrings.working,
        "unavailable" to AppStrings.notAvailable
    )
    val availabilityColors = mapOf(
        "available" to AppColors.Success,
        "working" to AppColors.Working,
        "unavailable" to AppColors.Error
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AppColors.Primary, AppColors.PrimaryLight)))
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 20.dp)) {
                AsyncImage(
                    model = p.photo,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                )
                Spacer(Modifier.height(12.dp))
                Text(p.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                if (p.serviceType.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Text(
                            AppStrings.serviceTypeName(p.serviceType),
                            fontSize = 12.sp, color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                // Skill level badge
                SkillBadge(p.skillLevel)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { i ->
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = if (i < p.rating.toInt()) Color(0xFFFFB300) else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text("%.1f".format(p.rating), fontSize = 13.sp, color = Color.White)
                }

                Spacer(Modifier.height(16.dp))

                // Availability selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availabilityOptions.forEach { opt ->
                        val isSelected = p.availability == opt
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 40.dp)
                                .clickable { vm.setAvailability(opt) }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp)) {
                                Text(
                                    availabilityLabels[opt] ?: opt,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) availabilityColors[opt] ?: AppColors.Primary else Color.White,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(Modifier.padding(16.dp)) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(Modifier.weight(1f), AppStrings.earnings, "৳ ${p.advance.toInt()}", AppColors.Primary, AppColors.PrimaryContainer)
                StatTile(Modifier.weight(1f), AppStrings.due, "৳ ${p.due.toInt()}", AppColors.Error, Color(0xFFFFEBEE))
                StatTile(Modifier.weight(1f), AppStrings.jobs, "${p.history.size}", Color(0xFF1565C0), Color(0xFFE3F2FD))
            }

            Spacer(Modifier.height(14.dp))

            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(AppStrings.personalInfo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                    Spacer(Modifier.height(14.dp))
                    InfoRow(Icons.Default.Phone, AppStrings.phoneNumber, p.phone)
                    if (p.email.isNotBlank()) {
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppColors.Divider)
                        InfoRow(Icons.Default.Email, AppStrings.emailAddress.removeSuffix(" (Optional)").removeSuffix(" (ঐচ্ছিক)"), p.email)
                    }
                    HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppColors.Divider)
                    InfoRow(Icons.Default.Person, AppStrings.nid, if (p.nid.isNotBlank()) maskNid(p.nid) else "—")
                    if (p.serviceType.isNotBlank()) {
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppColors.Divider)
                        InfoRow(Icons.Default.List, AppStrings.service, p.serviceType)
                    }
                    HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppColors.Divider)
                    InfoRow(Icons.Default.Star, AppStrings.baseFeeShort, "৳ ${p.baseFee.toInt()} ${AppStrings.perService}")
                }
            }

            // Certificate card
            Spacer(Modifier.height(12.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(AppStrings.certificate, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                    Spacer(Modifier.height(10.dp))
                    if (p.certificate.isNotBlank()) {
                        AsyncImage(
                            model = p.certificate,
                            contentDescription = AppStrings.certificate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(AppColors.Background, RoundedCornerShape(10.dp))
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(AppColors.Background, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(AppStrings.noCertificate, fontSize = 13.sp, color = AppColors.TextSecondary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Language toggle
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppStrings.language, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AppColors.PrimaryContainer,
                        modifier = Modifier.clickable { vm.toggleLanguage() }
                    ) {
                        Text(
                            if (vm.currentLang() == AppLanguage.BN) "English" else "বাংলা",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { nav.navigate(Screen.EditProfile.route) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(AppStrings.editProfile, fontSize = 15.sp)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    vm.logout()
                    nav.navigate(Screen.Entry.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Error)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = AppColors.Error, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(AppStrings.signOut, fontSize = 15.sp, color = AppColors.Error)
            }

            if (p.history.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(AppStrings.serviceHistory, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${p.history.size} ${AppStrings.jobs}", fontSize = 12.sp, color = AppColors.TextSecondary)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.Error.copy(alpha = 0.1f),
                            modifier = Modifier.clickable { showDeleteConfirm = true }
                        ) {
                            Text(
                                "🗑 মুছুন",
                                fontSize = 11.sp,
                                color = AppColors.Error,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                p.history.reversed().forEach { h ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(36.dp).background(AppColors.PrimaryContainer, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.List, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(h.description, fontSize = 14.sp, color = AppColors.TextPrimary)
                            }
                            Text("৳ ${h.earning.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppColors.Success)
                        }
                    }
                }
            } else {
                Spacer(Modifier.height(32.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.List, null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(AppStrings.noHistory, fontSize = 14.sp, color = AppColors.TextSecondary)
                        Text(AppStrings.acceptJobsHint, fontSize = 12.sp, color = Color(0xFFBDBDBD))
                    }
                }
            }

            // ── Reviews ───────────────────────────────────────────────────────
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("গ্রাহক রিভিউ", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Text("${reviews.size} রিভিউ", fontSize = 12.sp, color = AppColors.TextSecondary)
            }
            Spacer(Modifier.height(8.dp))
            if (reviews.isEmpty()) {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(1.dp)) {
                    Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("এখনো কোনো রিভিউ নেই", fontSize = 13.sp, color = AppColors.TextSecondary)
                    }
                }
            } else {
                reviews.forEach { review ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(review.clientName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                                Text("${"⭐".repeat(review.rating)}", fontSize = 13.sp)
                            }
                            if (review.serviceType.isNotBlank()) Text(review.serviceType, fontSize = 11.sp, color = AppColors.TextSecondary)
                            if (review.comment.isNotBlank()) Text("\"${review.comment}\"", fontSize = 13.sp, color = AppColors.TextPrimary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Delete history confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title   = { Text("ইতিহাস মুছে ফেলুন?") },
            text    = { Text("সব সেবার ইতিহাস এবং আয়ের রেকর্ড মুছে যাবে। এটি পূর্বাবস্থায় ফেরানো যাবে না।") },
            confirmButton = {
                Button(
                    onClick = { vm.clearHistory(); showDeleteConfirm = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) { Text("হ্যাঁ, মুছুন") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) { Text("বাতিল") }
            }
        )
    }
}

private fun maskNid(nid: String): String {
    if (nid.length < 4) return nid
    return nid.take(2) + "•".repeat(nid.length - 4) + nid.takeLast(2)
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(34.dp).background(AppColors.PrimaryContainer, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = AppColors.TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
        }
    }
}

@Composable
fun SkillBadge(skillLevel: String) {
    val (icon, color, bgColor) = when (skillLevel) {
        "professional" -> Triple("⚡", Color(0xFF1A237E), Color(0xFFE8EAF6))
        "expert"       -> Triple("🏆", Color(0xFFE65100), Color(0xFFFFF3E0))
        else           -> Triple("🔧", Color(0xFF757575), Color(0xFFF5F5F5))
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bgColor) {
        Row(
            modifier              = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 12.sp)
            Text(
                AppStrings.skillLevelName(skillLevel),
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color      = color
            )
        }
    }
}

@Composable
private fun StatTile(modifier: Modifier, label: String, value: String, textColor: Color, bg: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, fontSize = 11.sp, color = textColor.copy(alpha = 0.7f))
        }
    }
}
