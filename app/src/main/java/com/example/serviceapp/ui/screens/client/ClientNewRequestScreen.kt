package com.example.serviceapp.ui.screens.client

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.ui.components.AreaPickerDialog
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.LocationHelper
import com.example.serviceapp.utils.ServiceData
import com.example.serviceapp.viewmodel.ClientViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClientNewRequestScreen(vm: ClientViewModel, nav: NavController) {
    var selectedCategoryId  by remember { mutableStateOf("") }
    var selectedProblems    by remember { mutableStateOf(setOf<String>()) }  // multiple
    var customNote          by remember { mutableStateOf("") }
    var address             by remember { mutableStateOf("") }
    var locationLat         by remember { mutableStateOf(0.0) }
    var locationLng         by remember { mutableStateOf(0.0) }
    var minRating           by remember { mutableStateOf(0.0) }
    var maxPrice            by remember { mutableStateOf(0.0) }
    var minSkillLevel       by remember { mutableStateOf("") }   // "" | "professional" | "expert"
    var locationLoading     by remember { mutableStateOf(false) }
    var showAreaPicker      by remember { mutableStateOf(false) }
    var selectedArea        by remember { mutableStateOf("") }

    val context  = LocalContext.current
    val scope    = rememberCoroutineScope()
    val purple   = Color(0xFF6A1B9A)
    val isBn     = AppStrings.lang == AppLanguage.BN

    val selectedCategory = ServiceData.categoryById(selectedCategoryId)

    // Highest severity problem type among selected problems
    val selectedProblemType = remember(selectedProblems, selectedCategoryId) {
        val types = selectedCategory?.problems
            ?.filter { p -> selectedProblems.contains(if (isBn) p.bnLabel else p.enLabel) }
            ?.map { it.problemType } ?: emptyList()
        when {
            "critical" in types -> "critical"
            "advanced" in types -> "advanced"
            else                -> "normal"
        }
    }

    // Combined description from selected problems + custom note
    val description = remember(selectedProblems, customNote) {
        val parts = selectedProblems.toList() + if (customNote.isNotBlank()) listOf(customNote) else emptyList()
        parts.joinToString(", ")
    }

    val canSubmit = selectedCategoryId.isNotEmpty() && selectedProblems.isNotEmpty() && address.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = purple, focusedLabelColor = purple, cursorColor = purple
    )

    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            locationLoading = true
            scope.launch { val r = LocationHelper.getLocationResult(context); if (r != null) { address = r.address; locationLat = r.lat; locationLng = r.lng }; locationLoading = false }
        }
    }

    var showLocationOffDialog by remember { mutableStateOf(false) }

    fun isLocationServiceEnabled(): Boolean {
        val lm = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun fetchLocation() {
        // Check if location services (GPS) are on
        if (!isLocationServiceEnabled()) {
            showLocationOffDialog = true
            return
        }
        val fine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            locationLoading = true
            scope.launch { val r = LocationHelper.getLocationResult(context); if (r != null) { address = r.address; locationLat = r.lat; locationLng = r.lng }; locationLoading = false }
        } else {
            locationPermLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Location off dialog
    if (showLocationOffDialog) {
        AlertDialog(
            onDismissRequest = { showLocationOffDialog = false },
            title   = { Text(if (AppStrings.lang == AppLanguage.BN) "লোকেশন বন্ধ আছে" else "Location is Off") },
            text    = { Text(if (AppStrings.lang == AppLanguage.BN) "আপনার ফোনের লোকেশন চালু করুন এবং আবার চেষ্টা করুন।" else "Please turn on your device location (GPS) and try again.") },
            confirmButton = {
                Button(onClick = {
                    showLocationOffDialog = false
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text(if (AppStrings.lang == AppLanguage.BN) "সেটিংস খুলুন" else "Open Settings")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLocationOffDialog = false }) {
                    Text(if (AppStrings.lang == AppLanguage.BN) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Area picker dialog (single-select for client location)
    if (showAreaPicker) {
        AreaPickerDialog(
            selected    = if (selectedArea.isNotBlank()) setOf(selectedArea) else emptySet(),
            multiSelect = false,
            accentColor = purple,
            title       = if (isBn) "এলাকা বেছে নিন" else "Choose Your Area",
            onDismiss   = { showAreaPicker = false },
            onConfirm   = { picked ->
                selectedArea = picked.firstOrNull() ?: ""
                if (selectedArea.isNotBlank()) address = selectedArea
                locationLat  = 0.0
                locationLng  = 0.0
                showAreaPicker = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF3E5F5))) {

        // Header
        Box(modifier = Modifier.fillMaxWidth().background(purple).statusBarsPadding().padding(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                Text(AppStrings.newRequestTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).imePadding().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── STEP 1: Category ─────────────────────────────────────────────
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("1", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White,
                            modifier = Modifier.background(purple, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp))
                        Text(AppStrings.serviceTypeLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    }
                    Spacer(Modifier.height(12.dp))

                    // 3-column category grid
                    val chunked = ServiceData.categories.chunked(3)
                    chunked.forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { cat ->
                                val isSelected = selectedCategoryId == cat.id
                                Surface(
                                    modifier = Modifier.weight(1f).clickable {
                                        selectedCategoryId = cat.id
                                        selectedProblems   = emptySet()
                                        customNote         = ""
                                    },
                                    shape  = RoundedCornerShape(12.dp),
                                    color  = if (isSelected) purple else Color(0xFFF3E5F5),
                                    tonalElevation = if (isSelected) 0.dp else 0.dp
                                ) {
                                    Column(
                                        Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(cat.icon, fontSize = 22.sp)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            if (isBn) cat.bnLabel else cat.enLabel,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color     = if (isSelected) Color.White else Color(0xFF424242),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                            // Fill empty slots in last row
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // ── STEP 2: Problem (multi-select) ───────────────────────────────
            AnimatedVisibility(visible = selectedCategory != null, enter = expandVertically(), exit = shrinkVertically()) {
                selectedCategory?.let { cat ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("2", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                    modifier = Modifier.background(purple, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp))
                                Column {
                                    Text(
                                        if (isBn) "সমস্যা বেছে নিন — ${cat.icon} ${cat.bnLabel}" else "Select Problems — ${cat.icon} ${cat.enLabel}",
                                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242)
                                    )
                                    Text(
                                        if (isBn) "একাধিক সমস্যা বেছে নিতে পারেন" else "You can select multiple problems",
                                        fontSize = 11.sp, color = Color(0xFF9E9E9E)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                cat.problems.forEach { prob ->
                                    val label    = if (isBn) prob.bnLabel else prob.enLabel
                                    val isSelPrb = selectedProblems.contains(label)
                                    FilterChip(
                                        selected = isSelPrb,
                                        onClick  = {
                                            // Toggle selection
                                            selectedProblems = if (isSelPrb)
                                                selectedProblems - label
                                            else
                                                selectedProblems + label
                                        },
                                        label = {
                                            Text(
                                                label,
                                                fontSize = 12.sp,
                                                color = if (prob.isWarning && !isSelPrb) Color(0xFFC62828) else LocalContentColor.current
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = if (prob.isWarning) Color(0xFFC62828) else purple,
                                            selectedLabelColor     = Color.White
                                        )
                                    )
                                }
                            }
                            // "Other" custom note — only visible when the Other chip is selected
                            val otherLabel = if (isBn) "অন্যান্য" else "Other"
                            if (selectedProblems.contains(otherLabel)) {
                                Spacer(Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = customNote,
                                    onValueChange = { customNote = it },
                                    placeholder = { Text(if (isBn) "অন্যান্য সমস্যা লিখুন..." else "Describe the other issue...", color = Color(0xFFBDBDBD)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = fieldColors,
                                    singleLine = false,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }

            // ── STEP 3: Address ──────────────────────────────────────────────
            AnimatedVisibility(visible = selectedCategoryId.isNotEmpty(), enter = expandVertically(), exit = shrinkVertically()) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("3", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                    modifier = Modifier.background(purple, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp))
                                Text(AppStrings.addressLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { fetchLocation() }, enabled = !locationLoading,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = purple),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    if (locationLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(13.dp), strokeWidth = 2.dp, color = purple)
                                    } else {
                                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(3.dp))
                                        Text(AppStrings.autoLocationBtn, fontSize = 11.sp)
                                    }
                                }
                                OutlinedButton(
                                    onClick = { showAreaPicker = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = purple),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(3.dp))
                                    Text(if (isBn) "এলাকা" else "Area", fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        // Selected area chip
                        if (selectedArea.isNotBlank()) {
                            androidx.compose.material3.InputChip(
                                selected = true,
                                onClick  = { selectedArea = ""; address = "" },
                                label    = { Text("📍 $selectedArea", fontSize = 12.sp) },
                                trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) },
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = purple.copy(alpha = 0.12f),
                                    selectedLabelColor = purple
                                ),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it; if (it != selectedArea) selectedArea = "" },
                            label = { Text(AppStrings.addressHint) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = purple) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            singleLine = true, colors = fieldColors
                        )
                    }
                }
            }

            // ── Provider Filters ─────────────────────────────────────────────
            AnimatedVisibility(visible = selectedCategoryId.isNotEmpty(), enter = expandVertically(), exit = shrinkVertically()) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(AppStrings.providerFilter, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                        Spacer(Modifier.height(10.dp))
                        // ── Rating slider (0.0 → 5.0 in 0.5 steps) ──────────────
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(AppStrings.minRatingLabel, fontSize = 12.sp, color = Color(0xFF757575))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (minRating > 0.0) purple else Color(0xFFF3E5F5)
                            ) {
                                Text(
                                    if (minRating == 0.0) AppStrings.anyFilter else "${"%.1f".format(minRating)}⭐+",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (minRating > 0.0) Color.White else Color(0xFF424242),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("0", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                            Slider(
                                value       = minRating.toFloat(),
                                onValueChange = { raw ->
                                    // snap to nearest 0.5
                                    minRating = ((raw * 2).roundToInt() / 2.0)
                                },
                                valueRange  = 0f..5f,
                                steps       = 9,   // 10 intervals → 0, 0.5, 1.0 … 5.0
                                modifier    = Modifier.weight(1f).padding(horizontal = 4.dp),
                                colors      = SliderDefaults.colors(
                                    activeTrackColor   = purple,
                                    thumbColor         = purple,
                                    inactiveTrackColor = purple.copy(alpha = 0.2f)
                                )
                            )
                            Text("5", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                        }
                        // Star row
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { i ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = when {
                                        i <= minRating.toInt()          -> Color(0xFFFFA000)
                                        i == minRating.toInt() + 1
                                            && minRating % 1.0 >= 0.5  -> Color(0xFFFFCC80)
                                        else                            -> Color(0xFFE0E0E0)
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        // ── Max price slider (0 → 3000 BDT in 100-step increments) ──
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(AppStrings.maxFeeLabel, fontSize = 12.sp, color = Color(0xFF757575))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (maxPrice > 0.0) purple else Color(0xFFF3E5F5)
                            ) {
                                Text(
                                    if (maxPrice == 0.0) AppStrings.anyFilter else "৳${maxPrice.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (maxPrice > 0.0) Color.White else Color(0xFF424242),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("0", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                            Slider(
                                value       = maxPrice.toFloat(),
                                onValueChange = { raw ->
                                    maxPrice = ((raw / 100).roundToInt() * 100.0)
                                },
                                valueRange  = 0f..3000f,
                                steps       = 29,  // 100 BDT per step → 0, 100 … 3000
                                modifier    = Modifier.weight(1f).padding(horizontal = 4.dp),
                                colors      = SliderDefaults.colors(
                                    activeTrackColor   = purple,
                                    thumbColor         = purple,
                                    inactiveTrackColor = purple.copy(alpha = 0.2f)
                                )
                            )
                            Text("৳3000", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(if (isBn) "মিস্ত্রির দক্ষতা স্তর" else "Skill Level", fontSize = 12.sp, color = Color(0xFF757575))
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                ""             to (if (isBn) "যেকোনো" else "Any"),
                                "professional" to (if (isBn) "⚡ প্রফেশনাল+" else "⚡ Professional+"),
                                "expert"       to (if (isBn) "🏆 এক্সপার্ট" else "🏆 Expert")
                            ).forEach { (level, label) ->
                                FilterChip(
                                    selected = minSkillLevel == level,
                                    onClick  = { minSkillLevel = level },
                                    label    = { Text(label, fontSize = 12.sp) },
                                    colors   = FilterChipDefaults.filterChipColors(selectedContainerColor = purple, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Submit button
        Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
            Button(
                onClick = {
                    vm.createRequest(selectedCategoryId, description.trim(), address.trim(), minRating, maxPrice, selectedProblemType, locationLat, locationLng, selectedArea, minSkillLevel) {
                        nav.navigate(Screen.ClientDashboard.route) {
                            popUpTo(Screen.ClientDashboard.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                enabled  = canSubmit && !vm.requestLoading,
                colors   = ButtonDefaults.buttonColors(containerColor = purple, disabledContainerColor = Color(0xFFBDBDBD))
            ) {
                if (vm.requestLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (canSubmit) AppStrings.sendRequestBtn else AppStrings.fillAllFields2, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
