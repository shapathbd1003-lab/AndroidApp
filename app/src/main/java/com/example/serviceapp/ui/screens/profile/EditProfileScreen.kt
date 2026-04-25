package com.example.serviceapp.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.ui.components.AvatarPicker
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(vm: MainViewModel, nav: NavController) {

    val p = vm.provider ?: return

    var name by remember { mutableStateOf(p.name) }
    var phone by remember { mutableStateOf(p.phone) }
    var email by remember { mutableStateOf(p.email) }
    var nid by remember { mutableStateOf(p.nid) }
    var baseFeeText by remember { mutableStateOf(p.baseFee.toInt().toString()) }
    var photo by remember { mutableStateOf(p.photo) }
    var certificate by remember { mutableStateOf(p.certificate) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { photo = it.toString() } }

    val certLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { certificate = it.toString() } }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AppColors.Primary,
        focusedLabelColor = AppColors.Primary,
        cursorColor = AppColors.Primary
    )
    val canSave = name.isNotBlank() && phone.isNotBlank() && nid.isNotBlank() &&
            (baseFeeText.toDoubleOrNull() ?: 0.0) > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.editProfile, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
        ) {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    SectionLabel(AppStrings.profilePhoto)
                    Spacer(Modifier.height(12.dp))
                    AvatarPicker(
                        selected = photo,
                        onSelect = { photo = it },
                        onUploadFromGallery = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = AppColors.Divider)
                    Spacer(Modifier.height(20.dp))

                    SectionLabel(AppStrings.updateInfo)
                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(AppStrings.fullName) },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp)) },
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
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(AppStrings.emailAddress) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nid,
                        onValueChange = { if (it.length <= 17) nid = it },
                        label = { Text(AppStrings.nidNumber) },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        supportingText = { Text("${nid.length}/17 digits", color = AppColors.TextSecondary, fontSize = 11.sp) }
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = baseFeeText,
                        onValueChange = { baseFeeText = it },
                        label = { Text(AppStrings.baseFee) },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
                    )

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = AppColors.Divider)
                    Spacer(Modifier.height(20.dp))

                    SectionLabel(AppStrings.certificateOpt)
                    Spacer(Modifier.height(10.dp))

                    if (certificate.isNotBlank()) {
                        coil.compose.AsyncImage(
                            model = certificate,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(AppColors.Background, RoundedCornerShape(12.dp))
                                .padding(4.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            certLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary)
                    ) {
                        Text(AppStrings.uploadCertificate, color = AppColors.Primary)
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (canSave) {
                                p.name = name.trim()
                                p.phone = phone.trim()
                                p.email = email.trim()
                                p.nid = nid.trim()
                                p.photo = photo
                                p.baseFee = baseFeeText.toDoubleOrNull() ?: p.baseFee
                                p.certificate = certificate
                                nav.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = canSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            disabledContainerColor = Color(0xFFBDBDBD)
                        )
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(AppStrings.saveChanges, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
}
