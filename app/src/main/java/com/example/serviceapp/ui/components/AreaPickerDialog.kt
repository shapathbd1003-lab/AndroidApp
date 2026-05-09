package com.example.serviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.serviceapp.utils.AppLanguage
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.utils.AreaData

/**
 * Area picker dialog.
 * multiSelect=true  → provider picks multiple service areas (confirm button)
 * multiSelect=false → client picks one area for their location (auto-closes on tap)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AreaPickerDialog(
    selected: Set<String>,
    multiSelect: Boolean = true,
    accentColor: Color,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    val isBn = AppStrings.lang == AppLanguage.BN
    var tempSelected by remember { mutableStateOf(selected) }
    var searchQuery  by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.fillMaxSize()) {

                // ── Header ───────────────────────────────────────────────────
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(accentColor, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        if (multiSelect && tempSelected.isNotEmpty()) {
                            Text(
                                if (isBn) "${tempSelected.size}টি এলাকা বেছে নেওয়া হয়েছে"
                                else "${tempSelected.size} area(s) selected",
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }

                // ── Search ───────────────────────────────────────────────────
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (isBn) "এলাকা খুঁজুন..." else "Search area...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        cursorColor = accentColor
                    )
                )

                // ── City + Area list ─────────────────────────────────────────
                val filteredCities = if (searchQuery.isBlank()) {
                    AreaData.cities
                } else {
                    AreaData.cities.mapNotNull { city ->
                        val matchedAreas = city.areas.filter {
                            it.contains(searchQuery, ignoreCase = true)
                        }
                        if (matchedAreas.isNotEmpty()) city.copy(areas = matchedAreas) else null
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCities) { city ->
                        Column {
                            // City header
                            Text(
                                if (isBn) "${city.bnName} / ${city.enName}"
                                else city.enName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            // Area chips
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                city.areas.forEach { area ->
                                    val isSelected = area in tempSelected
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            if (multiSelect) {
                                                tempSelected = if (isSelected)
                                                    tempSelected - area
                                                else
                                                    tempSelected + area
                                            } else {
                                                onConfirm(setOf(area))
                                            }
                                        },
                                        label = { Text(area, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = accentColor,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                            HorizontalDivider(Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE))
                        }
                    }
                }

                // ── Buttons (multi-select only) ──────────────────────────────
                if (multiSelect) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { tempSelected = emptySet() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (isBn) "সব বাতিল" else "Clear All",
                                fontSize = 13.sp
                            )
                        }
                        Button(
                            onClick = { onConfirm(tempSelected) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text(
                                if (isBn) "সেভ করুন" else "Save",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
