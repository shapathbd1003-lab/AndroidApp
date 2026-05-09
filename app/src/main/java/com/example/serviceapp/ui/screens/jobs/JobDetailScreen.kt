package com.example.serviceapp.ui.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.ui.components.OpenMapsButton
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun JobDetailScreen(id: String, vm: MainViewModel, nav: NavController) {

    val job = vm.jobs.find { it.id == id } ?: run {
        android.util.Log.w("JobFlow", "JobDetailScreen: job $id not found in jobs list")
        Box(Modifier.fillMaxSize().background(AppColors.Background), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(AppStrings.jobNoLonger, color = AppColors.TextSecondary)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { nav.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                    Text(AppStrings.goBack)
                }
            }
        }
        return
    }

    // Log every recompose so we can see status + button changes in Logcat
    android.util.Log.d("JobFlow", "JobDetailScreen recompose: id=${id.takeLast(6)} status=${job.status} hasPoints=${vm.hasEnoughPoints}")

    var showPriceDialog by remember { mutableStateOf(false) }
    var customPrice     by remember { mutableStateOf(vm.provider?.baseFee?.toString() ?: "") }

    // Price negotiation dialog
    if (showPriceDialog) {
        AlertDialog(
            onDismissRequest = { showPriceDialog = false },
            title   = { Text(AppStrings.setJobPrice) },
            text    = {
                OutlinedTextField(
                    value = customPrice,
                    onValueChange = { customPrice = it },
                    label = { Text("৳ ${AppStrings.agreedPriceLabel}") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    customPrice.toDoubleOrNull()?.let { vm.setAgreedPrice(job.id, it) }
                    showPriceDialog = false
                }) { Text(AppStrings.saveChanges) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPriceDialog = false }) { Text(AppStrings.cancelBtn) }
            }
        )
    }

    Column(Modifier.fillMaxSize().background(AppColors.Background)) {

        // Header
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(AppColors.Primary, AppColors.PrimaryLight)))
                .statusBarsPadding()
                .padding(top = 4.dp, bottom = 16.dp, start = 4.dp, end = 16.dp)
        ) {
            Column {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(AppStrings.jobDetail, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    // Status badge
                    val (statusColor, statusLabel) = when (job.status) {
                        "awaiting"   -> Color(0xFFFFF8E1) to AppStrings.awaitingClientApproval
                        "agreed"     -> Color(0xFFE3F2FD) to AppStrings.clientAgreed
                        "on_the_way" -> Color(0xFFE8EAF6) to AppStrings.onTheWayStatus
                        "arrived"    -> Color(0xFFE8F5E9) to AppStrings.arrivedStatus
                        "working"    -> Color(0xFFFFF3E0) to AppStrings.workingStatus
                        "finished"   -> Color(0xFFE8F5E9) to AppStrings.finishedStatus
                        else         -> Color(0xFFE8EAF6) to AppStrings.pending
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.9f)) {
                        Text(statusLabel, fontSize = 11.sp, color = Color(0xFF212121), fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
                    }
                }
            }
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Job info card
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(3.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text(job.description, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                    if (job.overview.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().background(AppColors.Background, RoundedCornerShape(10.dp)).padding(12.dp)) {
                            Column {
                                Text(AppStrings.problemOverview, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                                Spacer(Modifier.height(4.dp))
                                Text(job.overview, fontSize = 13.sp, color = AppColors.TextPrimary, lineHeight = 19.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    InfoRow(Icons.Default.LocationOn, AppStrings.address, job.address, Color(0xFF1565C0))
                    Spacer(Modifier.height(10.dp))
                    InfoRow(Icons.Default.Phone, AppStrings.contact, job.phone, Color(0xFF6A1B9A))
                }
            }

            // Price card — only before client confirms (agreed state); hidden once provider is on the way
            if (job.status == "agreed") {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.PrimaryContainer)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(AppStrings.agreedPriceLabel, fontSize = 12.sp, color = AppColors.TextSecondary)
                            Text("৳ ${(vm.provider?.baseFee ?: 0.0).toInt()} ${AppStrings.perService}",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                        }
                        OutlinedButton(onClick = { showPriceDialog = true }, shape = RoundedCornerShape(8.dp)) {
                            Text(AppStrings.setJobPrice, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Completed card
            if (job.status == "finished") {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("☑️", fontSize = 24.sp)
                        Column {
                            Text(AppStrings.finishedStatus, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.Success)
                            Text("৳ ${(vm.provider?.baseFee ?: 0.0).toInt()} ${AppStrings.earned}",
                                fontSize = 13.sp, color = AppColors.Success)
                        }
                    }
                }
            }
        }

        // Action buttons
        Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
            when (job.status) {
                "pending" -> {
                    if (!vm.hasEnoughPoints) {
                        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFFFEBEE)) {
                            Text(AppStrings.insufficientPoints, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(14.dp))
                        }
                    } else {
                        Button(onClick = { vm.accept(job); nav.popBackStack() },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                            Text(AppStrings.acceptJob, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                "awaiting" -> Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFFFF8E1)) {
                    Text(AppStrings.awaitingClientApproval, color = Color(0xFFE65100), fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(14.dp))
                }
                "agreed" -> Button(onClick = { vm.markOnTheWay(job.id) },
                    modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                    Text(AppStrings.markOnTheWay, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                "on_the_way" -> Button(onClick = { vm.markArrived(job.id) },
                    modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text(AppStrings.markArrived, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                "arrived" -> Button(onClick = { vm.markWorking(job.id) },
                    modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))) {
                    Text(AppStrings.markWorking, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                "working" -> Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFFFF8E1)) {
                    Text(AppStrings.waitingClientFinish, color = Color(0xFFE65100),
                        fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(14.dp))
                }
                "finished" -> Surface(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFE8F5E9)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("☑️", fontSize = 20.sp)
                        Text(AppStrings.finishedStatus, color = AppColors.Success,
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, iconTint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(iconTint.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = AppColors.TextSecondary)
            Text(value, fontSize = 14.sp, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}
