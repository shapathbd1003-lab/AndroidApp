package com.example.serviceapp.ui.screens.jobs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.ui.components.FakeMapView
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun JobDetailScreen(id: String, vm: MainViewModel, nav: NavController) {

    val job = vm.jobs.find { it.id == id } ?: run {
        Box(
            Modifier.fillMaxSize().background(AppColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(AppStrings.jobNoLonger, color = AppColors.TextSecondary)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { nav.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) { Text(AppStrings.goBack) }
            }
        }
        return
    }

    val isDone = job.status == "done"
    val baseFee = vm.provider?.baseFee ?: 0.0

    Column(Modifier.fillMaxSize().background(AppColors.Background)) {

        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AppColors.Primary, AppColors.PrimaryLight)))
                .statusBarsPadding()
                .padding(top = 4.dp, bottom = 20.dp, start = 4.dp, end = 16.dp)
        ) {
            Column {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        AppStrings.jobDetail,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isDone) AppColors.Success else Color(0xFF1565C0)
                    ) {
                        Text(
                            if (isDone) "✓ ${AppStrings.completed}" else "● ${AppStrings.pending}",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(job.description, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)

                    if (job.overview.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(AppColors.Background, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(AppStrings.problemOverview, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                                Spacer(Modifier.height(4.dp))
                                Text(job.overview, fontSize = 13.sp, color = AppColors.TextPrimary, lineHeight = 19.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    InfoRow(Icons.Default.LocationOn, AppStrings.address, job.address, Color(0xFF1565C0))
                    Spacer(Modifier.height(12.dp))
                    InfoRow(Icons.Default.Phone, AppStrings.contact, job.phone, Color(0xFF6A1B9A))
                    Spacer(Modifier.height(12.dp)) }
            }

            Spacer(Modifier.height(16.dp))

            if (!isDone) {
                Button(
                    onClick = { vm.accept(job) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(AppStrings.acceptJob, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = AppColors.Success, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${AppStrings.jobAccepted} · ৳ ${baseFee.toInt()} ${AppStrings.earned}",
                            color = AppColors.Success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(AppStrings.jobLocation, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Spacer(Modifier.height(8.dp))
                FakeMapView(address = job.address, modifier = Modifier.fillMaxWidth().height(220.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, iconTint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).background(iconTint.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = AppColors.TextSecondary)
            Text(value, fontSize = 14.sp, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}
