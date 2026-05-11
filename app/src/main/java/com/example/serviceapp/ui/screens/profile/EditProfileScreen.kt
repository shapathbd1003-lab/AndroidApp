package com.example.serviceapp.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.ui.components.AreaPickerDialog
import com.example.serviceapp.ui.components.AvatarPicker
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(vm: MainViewModel, nav: NavController) {

    val p = vm.provider ?: return
    val isBn = AppStrings.lang == AppLanguage.BN

    var name by remember { mutableStateOf(p.name) }
    var phone by remember { mutableStateOf(p.phone) }
    var email by remember { mutableStateOf(p.email) }
    var nid by remember { mutableStateOf(p.nid) }
    var baseFeeText by remember { mutableStateOf(p.baseFee.toInt().toString()) }
    var photo by remember { mutableStateOf(p.photo) }
    var certificate by remember { mutableStateOf(p.certificate) }
    var selectedAreas by remember { mutableStateOf(p.coveredAreas.toSet()) }
    var showAreaPicker by remember { mutableStateOf(false) }

    if (showAreaPicker) {
        AreaPickerDialog(
            selected    = selectedAreas,
            multiSelect = true,
            accentColor = AppColors.Primary,
            title       = if (isBn) "সেবা এলাকা বেছে নিন" else "Select Service Areas",
            onDismiss   = { showAreaPicker = false },
            onConfirm   = { areas ->
                selectedAreas = areas
                showAreaPicker = false
                vm.updateCoveredAreas(areas.toList())
            }
        )
    }

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

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = AppColors.Divider)
                    Spacer(Modifier.height(20.dp))

                    // ── Service Area ─────────────────────────────────────────
                    SectionLabel(if (isBn) "সেবা এলাকা" else "Service Area")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (isBn) "কোন কোন এলাকায় কাজ করবেন? খালি রাখলে সব এলাকার কাজ দেখাবে।"
                        else "Which areas do you serve? Leave empty to see jobs from all areas.",
                        fontSize = 12.sp, color = AppColors.TextSecondary
                    )
                    Spacer(Modifier.height(10.dp))
                    // Selected area chips
                    if (selectedAreas.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            selectedAreas.sorted().forEach { area ->
                                InputChip(
                                    selected = true,
                                    onClick  = {
                                        val updated = selectedAreas - area
                                        selectedAreas = updated
                                        vm.updateCoveredAreas(updated.toList())
                                    },
                                    label = { Text(area, fontSize = 12.sp) },
                                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) },
                                    colors = InputChipDefaults.inputChipColors(
                                        selectedContainerColor = AppColors.PrimaryContainer,
                                        selectedLabelColor = AppColors.Primary
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppColors.PrimaryContainer,
                            modifier = Modifier.clickable { showAreaPicker = true }
                        ) {
                            Row(
                                Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = AppColors.Primary, modifier = Modifier.size(16.dp))
                                Text(
                                    if (isBn) "+ এলাকা যোগ করুন" else "+ Add Areas",
                                    fontSize = 13.sp, color = AppColors.Primary, fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (selectedAreas.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AppColors.Error.copy(alpha = 0.1f),
                                modifier = Modifier.clickable {
                                    selectedAreas = emptySet()
                                    vm.updateCoveredAreas(emptyList())
                                }
                            ) {
                                Text(
                                    if (isBn) "সব বাতিল" else "Clear All",
                                    fontSize = 12.sp, color = AppColors.Error,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Error banner
                    if (vm.saveError.isNotBlank()) {
                        Surface(
                            color = AppColors.Error.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                vm.saveError,
                                color = AppColors.Error, fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            if (canSave && !vm.saveLoading) {
                                val newFee = baseFeeText.toDoubleOrNull() ?: p.baseFee
                                // Update local model immediately for responsive UI
                                p.name    = name.trim()
                                p.phone   = phone.trim()
                                p.email   = email.trim()
                                p.nid     = nid.trim()
                                p.baseFee = newFee
                                vm.saveProfile(
                                    name.trim(), phone.trim(), email.trim(), nid.trim(),
                                    photo, newFee, certificate
                                ) {
                                    // Navigate only after successful upload + Firestore write
                                    nav.popBackStack()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = canSave && !vm.saveLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            disabledContainerColor = Color(0xFFBDBDBD)
                        )
                    ) {
                        if (vm.saveLoading) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(AppStrings.saveChanges, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
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
