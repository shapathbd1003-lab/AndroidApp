package com.example.serviceapp.ui.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
                    if (pendingCount > 0) "$pendingCount ${AppStrings.pending}"
                    else AppStrings.jobsAppear,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
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
                        job      = job,
                        onAccept = { vm.accept(job) },
                        onClick  = { nav.navigate(Screen.JobDetail.createRoute(job.id)) }
                    )
                }
            }
        }
    }
}
