package com.example.serviceapp.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.serviceapp.ui.components.AreaPickerDialog
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.verticalScrollbar
import com.example.serviceapp.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(vm: MainViewModel, nav: NavController) {

    val p = vm.provider ?: return
    val isBn = AppStrings.lang == AppLanguage.BN

    val liveJobCount = vm.jobs.count { it.status in listOf("awaiting", "agreed", "on_the_way", "arrived", "working") }
    val pendingCount = vm.jobs.count { it.status == "pending" }
    val completedJobs = p.history.size

    // The one job the provider is actively working on (highest priority status first)
    val activeJob = vm.jobs.firstOrNull {
        it.status in listOf("working", "arrived", "on_the_way", "agreed", "awaiting")
    }

    var showAreaPicker by remember { mutableStateOf(false) }
    if (showAreaPicker) {
        AreaPickerDialog(
            selected    = p.coveredAreas.toSet(),
            multiSelect = true,
            accentColor = AppColors.Primary,
            title       = if (isBn) "সেবা এলাকা বেছে নিন" else "Select Service Areas",
            onDismiss   = { showAreaPicker = false },
            onConfirm   = { areas -> vm.updateCoveredAreas(areas.toList()); showAreaPicker = false }
        )
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(scrollState)
            .verticalScrollbar(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AppColors.Primary, AppColors.PrimaryLight)))
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = p.photo,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp).clip(CircleShape)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(AppStrings.welcomeBack, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(p.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (p.serviceType.isNotBlank()) {
                                Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                                    Text(
                                        AppStrings.serviceTypeName(p.serviceType),
                                        fontSize = 10.sp, color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            com.example.serviceapp.ui.screens.profile.SkillBadge(p.skillLevel)
                            // Points badge
                            val pts = vm.points
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                                color = if (pts >= 400) Color.White.copy(alpha = 0.2f) else Color(0xFFC62828).copy(alpha = 0.8f)
                            ) {
                                Text("⭐ $pts ${AppStrings.pointsLabel}", fontSize = 10.sp, color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                    }
                }

                // Area filter indicator
                if (p.coveredAreas.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.FilterList, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(13.dp))
                        Text(
                            p.coveredAreas.take(3).joinToString(", ") +
                                if (p.coveredAreas.size > 3) " +${p.coveredAreas.size - 3}" else "",
                            fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

            }
        }

        Column(Modifier.padding(16.dp)) {

            Text(AppStrings.overview, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.weight(1f), AppStrings.rating, if (p.ratingCount > 0) "%.1f".format(p.rating) else "—", Icons.Default.Star,
                    Color(0xFFFFF3E0), Color(0xFFFF8F00))
                StatCard(Modifier.weight(1f), AppStrings.earnings, "৳ ${p.advance.toInt()}", Icons.Default.List,
                    AppColors.PrimaryContainer, AppColors.Primary)
                StatCard(Modifier.weight(1f), AppStrings.due, "৳ ${p.due.toInt()}", Icons.Default.List,
                    Color(0xFFFFEBEE), AppColors.Error)
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoTile(Modifier.weight(1f), AppStrings.liveJobs,  "$liveJobCount",  AppColors.PrimaryContainer, AppColors.Primary)
                InfoTile(Modifier.weight(1f), AppStrings.completed,  "$completedJobs", Color(0xFFE8F5E9),           AppColors.Success)
                InfoTile(Modifier.weight(1f), AppStrings.availableJobs, "$pendingCount", Color(0xFFFFF3E0),          Color(0xFFE65100))
            }

            // ── Active job card ───────────────────────────────────────────────
            if (activeJob != null) {
                Spacer(Modifier.height(16.dp))
                val (statusBg, statusFg, statusEmoji, statusLabel) = when (activeJob.status) {
                    "working"    -> listOf(Color(0xFFFFF3E0), Color(0xFFE65100), "🔧", if (isBn) "কাজ চলছে" else "Working")
                    "arrived"    -> listOf(Color(0xFFE8F5E9), Color(0xFF2E7D32), "🏠", if (isBn) "পৌঁছে গেছে" else "Arrived")
                    "on_the_way" -> listOf(Color(0xFFE8EAF6), Color(0xFF1A237E), "🛵", if (isBn) "যাচ্ছে" else "On the Way")
                    "agreed"     -> listOf(Color(0xFFE3F2FD), Color(0xFF1565C0), "✅", if (isBn) "নিশ্চিত" else "Confirmed")
                    else         -> listOf(Color(0xFFFFF8E1), Color(0xFFE65100), "⏳", if (isBn) "অপেক্ষায়" else "Awaiting")
                }
                Text(
                    if (isBn) "সক্রিয় কাজ" else "Active Job",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary
                )
                Spacer(Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { nav.navigate(Screen.JobDetail.createRoute(activeJob.id)) },
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = AppColors.Surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            Modifier.size(52.dp).background(statusBg as Color, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text(statusEmoji as String, fontSize = 24.sp) }
                        Column(Modifier.weight(1f)) {
                            Text(activeJob.description, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                            Spacer(Modifier.height(2.dp))
                            Text(activeJob.address, fontSize = 12.sp, color = AppColors.TextSecondary, maxLines = 1)
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = statusBg) {
                            Text(
                                statusLabel as String, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                color = statusFg as Color,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // ── Service area card ─────────────────────────────────────────────
            Spacer(Modifier.height(16.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.LocationOn, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                            Text(
                                if (isBn) "সেবা এলাকা" else "Service Areas",
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.PrimaryContainer,
                            modifier = Modifier.clickable { showAreaPicker = true }
                        ) {
                            Text(
                                if (isBn) "+ সম্পাদনা" else "+ Edit",
                                fontSize = 12.sp, color = AppColors.Primary, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                    if (p.coveredAreas.isEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (isBn) "সব এলাকায় কাজ করছেন" else "Serving all areas",
                            fontSize = 12.sp, color = AppColors.TextSecondary
                        )
                    } else {
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            p.coveredAreas.forEach { area ->
                                Surface(shape = RoundedCornerShape(20.dp), color = AppColors.PrimaryContainer) {
                                    Text(area, fontSize = 11.sp, color = AppColors.Primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                                }
                            }
                        }
                    }
                }
            }

            // ── Test button: add points quickly ──────────────────────────────
            Spacer(Modifier.height(12.dp))
            androidx.compose.material3.OutlinedButton(
                onClick = { vm.addTestPoints(500) },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFE65100)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE65100).copy(alpha = 0.5f))
            ) {
                Text("🧪 +500 ${AppStrings.pointsLabel} (test)", fontSize = 13.sp)
            }

            if (p.history.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(AppStrings.recentActivity, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                p.history.takeLast(3).reversed().forEach { h ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(h.description, fontSize = 14.sp, color = AppColors.TextPrimary)
                            Text("৳ ${h.earning.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.Success)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, iconBg: Color, iconTint: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(iconBg), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            Text(label, fontSize = 12.sp, color = AppColors.TextSecondary)
        }
    }
}

@Composable
private fun InfoTile(modifier: Modifier, label: String, value: String, bg: Color, textColor: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, fontSize = 12.sp, color = textColor.copy(alpha = 0.7f))
        }
    }
}
