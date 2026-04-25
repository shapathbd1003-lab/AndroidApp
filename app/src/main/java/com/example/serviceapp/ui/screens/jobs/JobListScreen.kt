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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.components.JobCard
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun JobListScreen(vm: MainViewModel, nav: NavController) {

    val pendingCount = vm.jobs.count { it.status == "pending" }
    val baseFee = vm.provider?.baseFee ?: 0.0

    Column(
        Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(AppColors.Primary)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Column {
                Text(AppStrings.availableJobs, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    "$pendingCount ${AppStrings.pending} · ${vm.jobs.size} ${AppStrings.jobsTotal}",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(AppStrings.simControls, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            if (vm.fastMode) AppStrings.fastModeLabel else AppStrings.normalModeLabel,
                            fontSize = 13.sp,
                            color = AppColors.TextSecondary
                        )
                        Text("Auto job interval", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                    }
                    Switch(
                        checked = vm.fastMode,
                        onCheckedChange = { vm.applySpeed(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AppColors.Primary
                        )
                    )
                }
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { vm.spawnJob() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryVar)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(AppStrings.spawnNow, fontSize = 13.sp)
                }
            }
        }

        if (vm.jobs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.List, null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(AppStrings.noJobs, fontSize = 16.sp, color = AppColors.TextSecondary)
                    Text(AppStrings.jobsAppear, fontSize = 13.sp, color = Color(0xFFBDBDBD))
                }
            }
        } else {
            LazyColumn {
                items(vm.jobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onAccept = { vm.accept(job) },
                        onClick = { nav.navigate(Screen.JobDetail.createRoute(job.id)) }
                    )
                }
            }
        }
    }
}
