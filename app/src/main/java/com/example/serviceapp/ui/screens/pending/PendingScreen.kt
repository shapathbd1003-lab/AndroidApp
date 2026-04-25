package com.example.serviceapp.ui.screens.pending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
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
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun PendingScreen(vm: MainViewModel, nav: NavController) {

    var isRejected by remember { mutableStateOf(false) }
    val provider = vm.provider

    DisposableEffect(Unit) {
        vm.startApprovalListener(
            onApproved = {
                nav.navigate(Screen.Main.route) {
                    popUpTo(Screen.Pending.route) { inclusive = true }
                }
            },
            onRejected = { isRejected = true }
        )
        onDispose { vm.stopApprovalListener() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(AppColors.Primary, AppColors.PrimaryLight))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isRejected) {
                    Text("❌", fontSize = 44.sp)
                } else {
                    Icon(
                        Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                if (isRejected) AppStrings.rejectedTitle else AppStrings.pendingTitle,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                if (isRejected) AppStrings.rejectedDesc else AppStrings.pendingDesc,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(32.dp))

            // Provider info card
            if (provider != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                provider.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(provider.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(provider.email, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isRejected) AppColors.Error.copy(alpha = 0.25f)
                                    else Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                if (isRejected) AppStrings.rejected else AppStrings.pendingBadge,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            if (!isRejected) {
                Spacer(Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White.copy(alpha = 0.7f),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        AppStrings.waitingForApproval,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            OutlinedButton(
                onClick = {
                    vm.logout()
                    nav.navigate(Screen.Entry.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(AppStrings.signOut, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
