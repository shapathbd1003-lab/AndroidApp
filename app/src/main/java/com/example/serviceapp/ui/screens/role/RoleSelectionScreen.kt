package com.example.serviceapp.ui.screens.role

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun RoleSelectionScreen(vm: MainViewModel, nav: NavController) {

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
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
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
                            if (vm.currentLang() == AppLanguage.BN) "EN" else "বাং",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Logo + Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    AppStrings.appName,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    AppStrings.selectRole,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }

            // Role cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 52.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleCard(
                    icon        = Icons.Default.Build,
                    title       = AppStrings.iAmProvider,
                    subtitle    = AppStrings.providerSubtitle,
                    iconBg      = AppColors.Primary,
                    borderColor = Color.White.copy(alpha = 0.5f),
                    onClick     = { nav.navigate(Screen.Entry.route) }
                )

                RoleCard(
                    icon        = Icons.Default.Person,
                    title       = AppStrings.iAmClient,
                    subtitle    = AppStrings.clientSubtitle,
                    iconBg      = Color(0xFF6A1B9A),
                    borderColor = Color.White.copy(alpha = 0.25f),
                    onClick     = { nav.navigate(Screen.ClientDemo.route) }
                )
            }
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBg: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.12f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBg.copy(alpha = 0.85f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title,    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.70f), modifier = Modifier.padding(top = 3.dp))
            }
            Text("→", fontSize = 22.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}
