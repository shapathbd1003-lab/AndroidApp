package com.example.serviceapp.ui.screens.entry

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun EntryScreen(vm: MainViewModel, nav: NavController) {

    LaunchedEffect(Unit) {
        vm.loadCurrentSession { loaded ->
            if (loaded) {
                val dest = if (vm.isApproved == true) Screen.Main.route else Screen.Pending.route
                nav.navigate(dest) {
                    popUpTo(Screen.RoleSelection.route) { inclusive = false }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AppColors.Primary, AppColors.PrimaryLight, Color(0xFF5C6BC0))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Language toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { vm.toggleLanguage() }) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            if (vm.currentLang() == com.example.serviceapp.utils.AppLanguage.BN) "EN" else "বাং",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Logo + branding
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    AppStrings.appName,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    AppStrings.appTagline,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Action buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { nav.navigate(Screen.Register.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        AppStrings.createAccount,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }

                OutlinedButton(
                    onClick = { nav.navigate(Screen.Login.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f))
                ) {
                    Text(
                        AppStrings.signIn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppStrings.alreadyHaveAccount, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = { nav.navigate(Screen.Login.route) }) {
                        Text(AppStrings.signIn, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
