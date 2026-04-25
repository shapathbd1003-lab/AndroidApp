package com.example.serviceapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.ClientViewModel

const val TEST_CLIENT_EMAIL    = "client@test.com"
const val TEST_CLIENT_PASSWORD = "123456"
const val TEST_CLIENT_NAME     = "Test User"
const val TEST_CLIENT_PHONE    = "01700000000"

@Composable
fun ClientEntryScreen(vm: ClientViewModel, nav: NavController) {

    LaunchedEffect(Unit) {
        vm.loadCurrentSession { loaded ->
            if (loaded) nav.navigate(Screen.ClientDashboard.route) {
                popUpTo(Screen.ClientEntry.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF9C27B0), Color(0xFFCE93D8))))
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
            Spacer(Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(90.dp).background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(20.dp))
                Text(AppStrings.appName, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(AppStrings.clientSubtitle, fontSize = 15.sp, color = Color.White.copy(alpha = 0.8f))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { nav.navigate(Screen.ClientRegister.route) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(AppStrings.createAccount2, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                }
                OutlinedButton(
                    onClick = { nav.navigate(Screen.ClientLogin.route) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f))
                ) {
                    Text(AppStrings.signInClient, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                // Test credentials card
                Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.15f), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(AppStrings.testAccount, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                        Spacer(Modifier.height(6.dp))
                        Text("Email: $TEST_CLIENT_EMAIL",       fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Text("Password: $TEST_CLIENT_PASSWORD", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    vm.loginAsync(TEST_CLIENT_EMAIL, TEST_CLIENT_PASSWORD) {
                                        nav.navigate(Screen.ClientDashboard.route) { popUpTo(Screen.ClientEntry.route) { inclusive = true } }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text(AppStrings.loginNow, fontSize = 12.sp, color = Color.White) }
                            OutlinedButton(
                                onClick = {
                                    vm.registerAsync(TEST_CLIENT_NAME, TEST_CLIENT_PHONE, TEST_CLIENT_EMAIL, TEST_CLIENT_PASSWORD) {
                                        nav.navigate(Screen.ClientDashboard.route) { popUpTo(Screen.ClientEntry.route) { inclusive = true } }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text(AppStrings.registerNow, fontSize = 12.sp, color = Color.White) }
                        }
                    }
                }

                TextButton(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                    Text(AppStrings.goBack2, color = Color.White.copy(alpha = 0.65f), fontSize = 14.sp)
                }
            }
        }
    }
}
