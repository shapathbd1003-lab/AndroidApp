package com.example.serviceapp.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun DashboardScreen(vm: MainViewModel) {

    val p = vm.provider ?: return
    val pendingJobs = vm.jobs.count { it.status == "pending" }
    val completedJobs = p.history.size

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
                        }
                    }
                    // Availability dot indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                availabilityColors[p.availability] ?: AppColors.Success,
                                CircleShape
                            )
                    )
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
                                .clickable { vm.setAvailability(opt) }
                        ) {
                            Text(
                                availabilityLabels[opt] ?: opt,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) availabilityColors[opt] ?: AppColors.Primary else Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Column(Modifier.padding(16.dp)) {

            Text(AppStrings.overview, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.weight(1f), AppStrings.rating, "%.1f".format(p.rating), Icons.Default.Star,
                    Color(0xFFFFF3E0), Color(0xFFFF8F00))
                StatCard(Modifier.weight(1f), AppStrings.earnings, "৳ ${p.advance.toInt()}", Icons.Default.List,
                    AppColors.PrimaryContainer, AppColors.Primary)
                StatCard(Modifier.weight(1f), AppStrings.due, "৳ ${p.due.toInt()}", Icons.Default.List,
                    Color(0xFFFFEBEE), AppColors.Error)
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoTile(Modifier.weight(1f), AppStrings.liveJobs, "$pendingJobs", AppColors.PrimaryContainer, AppColors.Primary)
                InfoTile(Modifier.weight(1f), AppStrings.completed, "$completedJobs", Color(0xFFE8F5E9), AppColors.Success)
                InfoTile(Modifier.weight(1f), AppStrings.generated, "${vm.totalGenerated}", Color(0xFFF3E5F5), Color(0xFF6A1B9A))
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
            Text(label, fontSize = 11.sp, color = AppColors.TextSecondary)
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
            Text(label, fontSize = 11.sp, color = textColor.copy(alpha = 0.7f))
        }
    }
}
