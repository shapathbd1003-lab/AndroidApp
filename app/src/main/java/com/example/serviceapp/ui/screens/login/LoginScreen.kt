package com.example.serviceapp.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun LoginScreen(vm: MainViewModel, nav: NavController) {

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AppColors.Primary,
        focusedLabelColor  = AppColors.Primary,
        cursorColor        = AppColors.Primary
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppColors.Primary, AppColors.PrimaryLight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(onClick = { nav.popBackStack() }, modifier = Modifier.padding(8.dp)) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text(AppStrings.signIn, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text(AppStrings.loginSubtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
                Spacer(Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {

                        // Error banner
                        if (vm.loginError.isNotEmpty()) {
                            Surface(
                                color = AppColors.Error.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    vm.loginError,
                                    color = AppColors.Error,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; vm.clearLoginError() },
                            label = { Text(AppStrings.emailAddress) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = AppColors.Primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = fieldColors
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; vm.clearLoginError() },
                            label = { Text(AppStrings.password) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = AppColors.Primary) },
                            trailingIcon = {
                                IconButton(onClick = { showPass = !showPass }) {
                                    Icon(
                                        if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        null, tint = AppColors.TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = fieldColors
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                vm.loginAsync(email.trim(), password) {
                                    val dest = if (vm.isApproved == true) Screen.Main.route else Screen.Pending.route
                                    nav.navigate(dest) {
                                        popUpTo(Screen.Entry.route) { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = email.isNotBlank() && password.isNotBlank() && !vm.loginLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Primary,
                                disabledContainerColor = Color(0xFFBDBDBD)
                            )
                        ) {
                            if (vm.loginLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(AppStrings.signIn, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(AppStrings.newProvider, fontSize = 13.sp, color = Color(0xFF9E9E9E))
                            Spacer(Modifier.width(4.dp))
                            TextButton(onClick = {
                                nav.navigate(Screen.Register.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }) {
                                Text(AppStrings.createAccount, fontSize = 13.sp, color = AppColors.Primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
