package com.example.serviceapp.ui.screens.jobs

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.components.JobCard
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@SuppressLint("MissingPermission")
@Composable
fun JobListScreen(vm: MainViewModel, nav: NavController) {

    val pendingCount = vm.jobs.count { it.status == "pending" }
    val context      = LocalContext.current

    // Get provider location once and sort jobs by distance
    LaunchedEffect(vm.jobs.size) {
        val fine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) vm.sortJobsByLocation(loc.latitude, loc.longitude)
                }
        }
    }

    Column(Modifier.fillMaxSize().background(AppColors.Background)) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(AppColors.Primary)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(AppStrings.availableJobs, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    // Points badge
                    val pts = vm.points
                    val ptsBg = if (pts >= 400) Color.White.copy(alpha = 0.2f) else Color(0xFFC62828).copy(alpha = 0.8f)
                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp), color = ptsBg) {
                        Text("⭐ $pts ${AppStrings.pointsLabel}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (pendingCount > 0) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(13.dp))
                        Text(
                            if (AppStrings.lang == com.example.serviceapp.utils.AppLanguage.BN)
                                "$pendingCount ${AppStrings.nearestSorted}"
                            else
                                "$pendingCount ${AppStrings.nearestSorted}",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f)
                        )
                    } else {
                        Text(AppStrings.jobsAppear, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        if (vm.jobs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(color = AppColors.Primary.copy(alpha = 0.4f), modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.height(4.dp))
                    Icon(Icons.Default.List, null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(40.dp))
                    Text(AppStrings.noJobs, fontSize = 16.sp, color = AppColors.TextSecondary)
                    Text(AppStrings.jobsAppear, fontSize = 13.sp, color = Color(0xFFBDBDBD))
                }
            }
        } else {
            LazyColumn {
                items(vm.jobs, key = { it.id }) { job ->
                    JobCard(
                        job            = job,
                        onAccept       = { vm.accept(job) },
                        onClick        = { nav.navigate(Screen.JobDetail.createRoute(job.id)) },
                        onMarkOnTheWay = if (job.status == "agreed") ({ vm.markOnTheWay(job.id) }) else null,
                        hasPoints      = vm.hasEnoughPoints
                    )
                }
            }
        }
    }
}
