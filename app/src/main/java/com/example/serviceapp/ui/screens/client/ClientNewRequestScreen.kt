package com.example.serviceapp.ui.screens.client

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.viewmodel.ClientViewModel

private val SERVICE_TYPES = listOf(
    "এসি রিপেয়ার", "প্লাম্বিং", "ইলেকট্রিক কাজ",
    "ডিপ ক্লিনিং", "রং করা", "কাঠের কাজ",
    "যন্ত্রপাতি মেরামত", "পোকামাকড় নিয়ন্ত্রণ"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClientNewRequestScreen(vm: ClientViewModel, nav: NavController) {
    var selectedService by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }
    var address         by remember { mutableStateOf("") }

    val purple     = Color(0xFF6A1B9A)
    val canSubmit  = selectedService.isNotEmpty() && description.isNotBlank() && address.isNotBlank()
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = purple, focusedLabelColor = purple, cursorColor = purple
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(purple)
                .statusBarsPadding()
                .padding(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Text("নতুন সেবার অনুরোধ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Service type
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("সেবার ধরন", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SERVICE_TYPES.forEach { type ->
                            FilterChip(
                                selected = selectedService == type,
                                onClick  = { selectedService = type },
                                label    = { Text(type, fontSize = 13.sp) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = purple,
                                    selectedLabelColor     = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Description
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("সমস্যার বিবরণ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("সমস্যাটি বিস্তারিত লিখুন...", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        maxLines = 5
                    )
                }
            }

            // Address
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("ঠিকানা", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("বাড়ি, রাস্তা, এলাকা") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = purple) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors
                    )
                }
            }
        }

        // Submit
        Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
            Button(
                onClick = {
                    vm.createRequest(selectedService, description.trim(), address.trim()) {
                        nav.navigate(Screen.ClientDashboard.route) {
                            popUpTo(Screen.ClientDashboard.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = canSubmit && !vm.requestLoading,
                colors = ButtonDefaults.buttonColors(containerColor = purple, disabledContainerColor = Color(0xFFBDBDBD))
            ) {
                if (vm.requestLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (canSubmit) "অনুরোধ পাঠান →" else "সব তথ্য পূরণ করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
