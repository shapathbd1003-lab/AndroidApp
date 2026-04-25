package com.example.serviceapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.serviceapp.viewmodel.ClientViewModel

@Composable
fun ClientRegisterScreen(vm: ClientViewModel, nav: NavController) {
    var name      by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirm   by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }

    val purple    = Color(0xFF6A1B9A)
    val canSubmit = name.isNotBlank() && phone.isNotBlank() && email.isNotBlank() &&
                    password.isNotBlank() && confirm.isNotBlank()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = purple, focusedLabelColor = purple, cursorColor = purple
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF9C27B0))))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Text("গ্রাহক নিবন্ধন", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                if (vm.registerError.isNotEmpty()) {
                    Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp)) {
                        Text(vm.registerError, color = Color(0xFFC62828), fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                OutlinedTextField(value = name, onValueChange = { name = it; vm.clearErrors() },
                    label = { Text("পুরো নাম") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = purple) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, colors = fieldColors)

                OutlinedTextField(value = phone, onValueChange = { phone = it },
                    label = { Text("ফোন নম্বর") },
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = purple) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = fieldColors)

                OutlinedTextField(value = email, onValueChange = { email = it },
                    label = { Text("ইমেইল") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = purple) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = fieldColors)

                OutlinedTextField(value = password, onValueChange = { password = it },
                    label = { Text("পাসওয়ার্ড") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = purple) },
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = fieldColors)

                OutlinedTextField(value = confirm, onValueChange = { confirm = it },
                    label = { Text("পাসওয়ার্ড নিশ্চিত করুন") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = purple) },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirm.isNotEmpty() && password != confirm,
                    supportingText = { if (confirm.isNotEmpty() && password != confirm) Text("পাসওয়ার্ড মিলছে না", color = Color(0xFFC62828)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = fieldColors)

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = {
                        if (password.length >= 6 && password == confirm) {
                            vm.registerAsync(name.trim(), phone.trim(), email.trim(), password) {
                                nav.navigate(Screen.ClientDashboard.route) {
                                    popUpTo(Screen.ClientEntry.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = canSubmit && !vm.registerLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = purple, disabledContainerColor = Color(0xFFBDBDBD))
                ) {
                    if (vm.registerLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("অ্যাকাউন্ট তৈরি করুন →", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
