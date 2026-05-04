package com.example.serviceapp.ui.screens.client

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.viewmodel.ClientViewModel

private val AVATAR_COLORS = listOf(
    0xFF1565C0.toInt() to "🧑",
    0xFF6A1B9A.toInt() to "👩",
    0xFF2E7D32.toInt() to "🧔",
    0xFFE65100.toInt() to "👱",
    0xFFC62828.toInt() to "🧕",
    0xFF37474F.toInt() to "👦",
)

@Composable
fun ClientRegisterScreen(vm: ClientViewModel, nav: NavController) {
    var name          by remember { mutableStateOf("") }
    var phone         by remember { mutableStateOf("") }
    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var confirm       by remember { mutableStateOf("") }
    var showPass      by remember { mutableStateOf(false) }
    var selectedAvatar by remember { mutableStateOf(AVATAR_COLORS[0]) }
    var photoUri      by remember { mutableStateOf("") }
    var localError    by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { photoUri = it.toString() } }

    val purple     = Color(0xFF6A1B9A)
    val phoneValid = phone.trim().length == 11
    val canSubmit  = name.isNotBlank() && phoneValid &&
                    email.isNotBlank() && password.isNotBlank() && confirm.isNotBlank()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = purple, focusedLabelColor = purple, cursorColor = purple
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF9C27B0))))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Text("গ্রাহক নিবন্ধন", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Error banner
                val anyError = localError.ifEmpty { vm.registerError }
                if (anyError.isNotEmpty()) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(anyError, color = Color(0xFFC62828), fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                // ── Avatar picker ──────────────────────────────────────
                Text("প্রোফাইল ছবি", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF424242))

                // Photo preview
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    if (photoUri.isNotBlank()) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(selectedAvatar.first), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selectedAvatar.second, fontSize = 36.sp)
                        }
                    }
                }

                // Preset avatars row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    AVATAR_COLORS.forEach { avatar ->
                        val isSelected = selectedAvatar == avatar && photoUri.isBlank()
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(avatar.first), CircleShape)
                                .then(if (isSelected) Modifier.border(3.dp, Color.White, CircleShape) else Modifier)
                                .clickable { selectedAvatar = avatar; photoUri = "" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(avatar.second, fontSize = 20.sp)
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = purple)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("গ্যালারি থেকে আপলোড", fontSize = 13.sp)
                }

                HorizontalDivider()

                // ── Form fields ────────────────────────────────────────
                OutlinedTextField(value = name, onValueChange = { name = it; localError = ""; vm.clearErrors() },
                    label = { Text("পুরো নাম *") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = purple) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, colors = fieldColors)

                OutlinedTextField(value = phone, onValueChange = { phone = it },
                    label = { Text("ফোন নম্বর *") },
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = purple) },
                    isError = phone.isNotEmpty() && !phoneValid,
                    supportingText = {
                        if (phone.isNotEmpty() && !phoneValid)
                            Text(AppStrings.phoneMustBe11, color = Color(0xFFC62828))
                        else Text("${phone.trim().length}/11", color = Color(0xFF9E9E9E))
                    },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = fieldColors)

                OutlinedTextField(value = email, onValueChange = { email = it; localError = ""; vm.clearErrors() },
                    label = { Text("ইমেইল *") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = purple) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = fieldColors)

                OutlinedTextField(value = password, onValueChange = { password = it; localError = "" },
                    label = { Text("পাসওয়ার্ড * (কমপক্ষে ৬ অক্ষর)") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = purple) },
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = password.isNotEmpty() && password.length < 6,
                    supportingText = { if (password.isNotEmpty() && password.length < 6) Text("কমপক্ষে ৬ অক্ষর দিন", color = Color(0xFFC62828)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = fieldColors)

                OutlinedTextField(value = confirm, onValueChange = { confirm = it; localError = "" },
                    label = { Text("পাসওয়ার্ড নিশ্চিত করুন *") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = purple) },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirm.isNotEmpty() && password != confirm,
                    supportingText = { if (confirm.isNotEmpty() && password != confirm) Text("পাসওয়ার্ড মিলছে না", color = Color(0xFFC62828)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = fieldColors)

                Button(
                    onClick = {
                        localError = ""
                        when {
                            password.length < 6   -> localError = "পাসওয়ার্ড কমপক্ষে ৬ অক্ষর হতে হবে।"
                            password != confirm    -> localError = "পাসওয়ার্ড মিলছে না।"
                            else -> {
                                val avatarStr = if (photoUri.isNotBlank()) photoUri
                                               else "${selectedAvatar.first}:${selectedAvatar.second}"
                                vm.registerAsync(name.trim(), phone.trim(), email.trim(), password, avatarStr) {
                                    nav.navigate(Screen.ClientDashboard.route) {
                                        popUpTo(Screen.ClientEntry.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = canSubmit && !vm.registerLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purple,
                        disabledContainerColor = Color(0xFFBDBDBD)
                    )
                ) {
                    if (vm.registerLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("অ্যাকাউন্ট তৈরি করুন →", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
