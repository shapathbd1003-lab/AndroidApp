package com.example.serviceapp.ui.screens.client

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.LocationHelper
import com.example.serviceapp.viewmodel.ClientViewModel
import kotlinx.coroutines.launch

private val SERVICE_TYPES = listOf(
    "এসি রিপেয়ার",
    "প্লাম্বিং",
    "ইলেকট্রিক কাজ",
    "ডিপ ক্লিনিং",
    "রং করা",
    "কাঠের কাজ",
    "যন্ত্রপাতি মেরামত",
    "পোকামাকড় নিয়ন্ত্রণ"
)

private val RATING_FILTERS_DATA = listOf(0.0, 3.5, 4.0, 4.5)
private val PRICE_FILTERS_DATA  = listOf(0.0, 500.0, 800.0, 1200.0)
private fun ratingLabel(v: Double) = if (v == 0.0) AppStrings.anyFilter else "${"%.1f".format(v)}+"
private fun priceLabel(v: Double)  = if (v == 0.0) AppStrings.anyFilter else "৳${v.toInt()}"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClientNewRequestScreen(vm: ClientViewModel, nav: NavController) {
    var selectedService by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }
    var address         by remember { mutableStateOf("") }
    var minRating       by remember { mutableStateOf(0.0) }
    var maxPrice        by remember { mutableStateOf(0.0) }
    var locationLoading by remember { mutableStateOf(false) }

    val context  = LocalContext.current
    val scope    = rememberCoroutineScope()
    val purple   = Color(0xFF6A1B9A)
    val canSubmit = selectedService.isNotEmpty() && description.isNotBlank() && address.isNotBlank()
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
            locationPermLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF3E5F5))) {
        Box(
            modifier = Modifier.fillMaxWidth().background(purple).statusBarsPadding().padding(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                Text(AppStrings.newRequestTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).imePadding().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Service type
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(AppStrings.serviceTypeLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppStrings.allServiceTypes.forEach { (bnKey, displayLabel) ->
                            FilterChip(
                                selected = selectedService == bnKey, onClick = { selectedService = bnKey },
                                label = { Text(displayLabel, fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = purple, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }

            // Description
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(AppStrings.problemDescLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        placeholder = { Text(AppStrings.problemDescHint, color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp), colors = fieldColors, maxLines = 5
                    )
                }
            }

            // Address
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(AppStrings.addressLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                        OutlinedButton(
                            onClick = { fetchLocation() }, enabled = !locationLoading,
                            shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = purple), modifier = Modifier.height(32.dp)
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

            // Filters
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(AppStrings.providerFilter, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(Modifier.height(12.dp))
                    Text(AppStrings.minRatingLabel, fontSize = 12.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RATING_FILTERS_DATA.forEach { value ->
                            FilterChip(
                                selected = minRating == value, onClick = { minRating = value },
                                label = { Text(ratingLabel(value), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = purple, selectedLabelColor = Color.White)
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(AppStrings.maxFeeLabel, fontSize = 12.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PRICE_FILTERS_DATA.forEach { value ->
                            FilterChip(
                                selected = maxPrice == value, onClick = { maxPrice = value },
                                label = { Text(priceLabel(value), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = purple, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
            Button(
                onClick = {
                    vm.createRequest(selectedService, description.trim(), address.trim(), minRating, maxPrice) {
                        nav.navigate(Screen.ClientDashboard.route) { popUpTo(Screen.ClientDashboard.route) { inclusive = true } }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp),
                enabled = canSubmit && !vm.requestLoading,
                colors = ButtonDefaults.buttonColors(containerColor = purple, disabledContainerColor = Color(0xFFBDBDBD))
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
