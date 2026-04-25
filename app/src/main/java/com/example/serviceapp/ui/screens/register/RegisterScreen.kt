package com.example.serviceapp.ui.screens.register

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.components.AvatarPicker
import com.example.serviceapp.ui.components.presetAvatars
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(vm: MainViewModel, nav: NavController) {

    var name            by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var nid             by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPass        by remember { mutableStateOf(false) }
    var baseFeeText     by remember { mutableStateOf("") }
    var selectedPhoto   by remember { mutableStateOf(presetAvatars.first()) }
    var selectedService by remember { mutableStateOf("") }
    var certificate     by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { selectedPhoto = it.toString() } }

    val certLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { certificate = it.toString() } }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AppColors.Primary,
        focusedLabelColor = AppColors.Primary,
        cursorColor = AppColors.Primary
    )
    val baseFee    = baseFeeText.toDoubleOrNull() ?: 0.0
    val canProceed = name.isNotBlank() && phone.isNotBlank() && email.isNotBlank() &&
            nid.isNotBlank() && selectedService.isNotEmpty() && baseFee > 0 &&
            password.isNotBlank() && confirmPassword.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppColors.Primary, AppColors.PrimaryLight)))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(AppStrings.appName, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(AppStrings.register, fontSize = 14.sp, color = Color.White.copy(alpha = 0.75f))

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(Modifier.padding(24.dp)) {

                SectionHeader(AppStrings.profilePhoto)
                Spacer(Modifier.height(12.dp))
                AvatarPicker(
                    selected = selectedPhoto,
                    onSelect = { selectedPhoto = it },
                    onUploadFromGallery = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(Modifier.height(20.dp))

                SectionHeader(AppStrings.personalInfo)
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(AppStrings.fullName) },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = AppColors.Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(AppStrings.phoneNumber) },
                    leadingIcon = { Icon(Icons.Default.Phone, null, tint = AppColors.Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; vm.clearRegisterError() },
                    label = { Text(AppStrings.emailRequired) },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = AppColors.Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; vm.clearRegisterError() },
                    label = { Text(AppStrings.password) },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = AppColors.Primary) },
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = AppColors.TextSecondary)
                        }
                    },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    supportingText = { Text(AppStrings.passwordHint, color = Color(0xFF9E9E9E), fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; vm.clearRegisterError() },
                    label = { Text(AppStrings.confirmPassword) },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = AppColors.Primary) },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                    supportingText = {
                        if (confirmPassword.isNotEmpty() && password != confirmPassword)
                            Text(AppStrings.passwordMismatch, color = AppColors.Error)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = nid,
                    onValueChange = { if (it.length <= 17) nid = it },
                    label = { Text(AppStrings.nidNumber) },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = AppColors.Primary) },
                    placeholder = { Text(AppStrings.nidHint, color = Color(0xFFBDBDBD)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    supportingText = { Text("${nid.length}/17 digits", color = Color(0xFF9E9E9E), fontSize = 11.sp) }
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = baseFeeText,
                    onValueChange = { baseFeeText = it },
                    label = { Text(AppStrings.baseFee) },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = AppColors.Primary) },
                    placeholder = { Text(AppStrings.baseFeeHint, color = Color(0xFFBDBDBD)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
                )

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(Modifier.height(20.dp))

                SectionHeader(AppStrings.serviceSpec)
                Text(AppStrings.serviceHint, fontSize = 12.sp, color = Color(0xFF9E9E9E))
                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    vm.serviceTypes.forEach { type ->
                        FilterChip(
                            selected = selectedService == type,
                            onClick = { selectedService = type },
                            label = { Text(type, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppColors.Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(Modifier.height(20.dp))

                SectionHeader(AppStrings.certificateOpt)
                Spacer(Modifier.height(10.dp))

                if (certificate.isNotBlank()) {
                    coil.compose.AsyncImage(
                        model = certificate,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color(0xFFF0F2F8), RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = if (certificate.isBlank()) "" else "✓ সার্টিফিকেট যোগ হয়েছে",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(AppStrings.certificate) },
                    placeholder = { Text(AppStrings.noCertificate, color = Color(0xFFBDBDBD)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            certLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = if (certificate.isNotBlank()) AppColors.Success else Color(0xFFBDBDBD),
                        focusedLabelColor = AppColors.Primary
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.AttachMoney,
                            null,
                            tint = if (certificate.isNotBlank()) AppColors.Success else AppColors.Primary
                        )
                    },
                    singleLine = true,
                    enabled = false
                )
                Spacer(Modifier.height(6.dp))
                androidx.compose.material3.TextButton(
                    onClick = {
                        certLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(AppStrings.uploadCertificate, color = AppColors.Primary, fontSize = 13.sp)
                }

                Spacer(Modifier.height(28.dp))

                if (vm.registerError.isNotEmpty()) {
                    Surface(
                        color = AppColors.Error.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(vm.registerError, color = AppColors.Error, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        if (password.length >= 6 && password == confirmPassword) {
                            vm.registerAsync(
                                name.trim(), phone.trim(), email.trim(), password,
                                nid.trim(), selectedPhoto, baseFee, selectedService, certificate
                            ) {
                                nav.navigate(Screen.Pending.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = canProceed && !vm.registerLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        disabledContainerColor = Color(0xFFBDBDBD)
                    )
                ) {
                    if (vm.registerLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            if (canProceed) AppStrings.getStarted else AppStrings.fillAll,
                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
}
