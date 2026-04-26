package com.example.serviceapp.ui.screens.client

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.LocationHelper
import com.example.serviceapp.utils.ServiceData
import com.example.serviceapp.viewmodel.ClientViewModel
import kotlinx.coroutines.launch

private val RATING_FILTERS_DATA = listOf(0.0, 3.5, 4.0, 4.5)
private val PRICE_FILTERS_DATA  = listOf(0.0, 500.0, 800.0, 1200.0)
private fun ratingLabel(v: Double) = if (v == 0.0) AppStrings.anyFilter else "${"%.1f".format(v)}+"
private fun priceLabel(v: Double)  = if (v == 0.0) AppStrings.anyFilter else "৳${v.toInt()}"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClientNewRequestScreen(vm: ClientViewModel, nav: NavController) {
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedProblem    by remember { mutableStateOf("") }
    var description        by remember { mutableStateOf("") }
    var address            by remember { mutableStateOf("") }
    var minRating          by remember { mutableStateOf(0.0) }
    var maxPrice           by remember { mutableStateOf(0.0) }
    var locationLoading    by remember { mutableStateOf(false) }
    var selectedProblemType by remember { mutableStateOf("normal") }

    val context  = LocalContext.current
    val scope    = rememberCoroutineScope()
    val purple   = Color(0xFF6A1B9A)
    val isBn     = AppStrings.lang == AppLanguage.BN

    val selectedCategory = ServiceData.categoryById(selectedCategoryId)
    val canSubmit = selectedCategoryId.isNotEmpty() && description.isNotBlank() && address.isNotBlank()

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
            scope.launch { address = LocationHelper.getCurrentAddress(context) ?: address; locationLoading = false }
        }
    }

    fun fetchLocation() {
        val fine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            locationLoading = true
            scope.launch { address = LocationHelper.getCurrentAddress(context) ?: address; locationLoading = false }
        } else {
            locationPermLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
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
                                        selectedProblem   = ""
                                        description       = ""
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

            // ── STEP 2: Problem ──────────────────────────────────────────────
            AnimatedVisibility(visible = selectedCategory != null, enter = expandVertically(), exit = shrinkVertically()) {
                selectedCategory?.let { cat ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("2", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                    modifier = Modifier.background(purple, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp))
                                Text(
                                    if (isBn) "সমস্যা বেছে নিন — ${cat.icon} ${cat.bnLabel}" else "Select Problem — ${cat.icon} ${cat.enLabel}",
                                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                cat.problems.forEach { prob ->
                                    val label    = if (isBn) prob.bnLabel else prob.enLabel
                                    val isSelPrb = selectedProblem == label
                                    FilterChip(
                                        selected = isSelPrb,
                                        onClick  = {
                                            selectedProblem     = label
                                            selectedProblemType = prob.problemType
                                            description = if (isBn) prob.bnLabel else prob.enLabel
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
                        }
                    }
                }
            }

            // ── STEP 3: Description ──────────────────────────────────────────
            AnimatedVisibility(visible = selectedCategoryId.isNotEmpty(), enter = expandVertically(), exit = shrinkVertically()) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("3", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                modifier = Modifier.background(purple, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp))
                            Text(AppStrings.problemDescLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                        }
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = description, onValueChange = { description = it },
                            placeholder = { Text(AppStrings.problemDescHint, color = Color(0xFFBDBDBD)) },
                            modifier = Modifier.fillMaxWidth().height(110.dp),
                            shape = RoundedCornerShape(12.dp), colors = fieldColors, maxLines = 5
                        )
                    }
                }
            }

            // ── STEP 4: Address ──────────────────────────────────────────────
            AnimatedVisibility(visible = selectedCategoryId.isNotEmpty(), enter = expandVertically(), exit = shrinkVertically()) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("4", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White,
                                    modifier = Modifier.background(purple, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp))
                                Text(AppStrings.addressLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                            }
                            OutlinedButton(
                                onClick = { fetchLocation() }, enabled = !locationLoading,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = purple),
                                modifier = Modifier.height(32.dp)
                            ) {
                                if (locationLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = purple)
                                    Spacer(Modifier.width(6.dp))
                                    Text(AppStrings.fetchingLocation, fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(AppStrings.autoLocationBtn, fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = address, onValueChange = { address = it },
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
                        Text(AppStrings.minRatingLabel, fontSize = 12.sp, color = Color(0xFF757575))
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RATING_FILTERS_DATA.forEach { v ->
                                FilterChip(
                                    selected = minRating == v, onClick = { minRating = v },
                                    label = { Text(ratingLabel(v), fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = purple, selectedLabelColor = Color.White)
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(AppStrings.maxFeeLabel, fontSize = 12.sp, color = Color(0xFF757575))
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PRICE_FILTERS_DATA.forEach { v ->
                                FilterChip(
                                    selected = maxPrice == v, onClick = { maxPrice = v },
                                    label = { Text(priceLabel(v), fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = purple, selectedLabelColor = Color.White)
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
                    vm.createRequest(selectedCategoryId, description.trim(), address.trim(), minRating, maxPrice, selectedProblemType) {
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
