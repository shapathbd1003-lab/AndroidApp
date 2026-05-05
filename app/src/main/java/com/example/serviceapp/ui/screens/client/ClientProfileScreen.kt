package com.example.serviceapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.data.model.ServiceRequest
import com.example.serviceapp.navigation.Screen
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.ClientViewModel

@Composable
fun ClientProfileScreen(vm: ClientViewModel, nav: NavController) {
    val purple             = Color(0xFF6A1B9A)
    val client             = vm.client
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val history = vm.requests.filter { it.status in listOf("completed", "cancelled", "no_provider") }
    val active  = vm.requests.filter { it.status in listOf("pending", "awaiting_approval", "accepted") }
    val avgRating = history.filter { it.rating > 0 }.map { it.rating }.average().let {
        if (it.isNaN()) 0.0 else it
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF3E5F5))) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF4A148C), Color(0xFF9C27B0))))
                .statusBarsPadding()
                .padding(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Text(AppStrings.clientProfileTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            IconButton(
                onClick = { vm.logout(); nav.navigate(Screen.RoleSelection.route) { popUpTo(0) { inclusive = true } } },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Color.White)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Avatar + info card
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Brush.verticalGradient(listOf(Color(0xFFEDE7F6), Color.White)))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(purple.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                client?.name?.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 32.sp, fontWeight = FontWeight.Bold, color = purple
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(client?.name ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                        Spacer(Modifier.height(4.dp))
                        Text(client?.phone ?: "", fontSize = 14.sp, color = Color(0xFF757575))
                        if ((client?.email ?: "").isNotBlank()) {
                            Text(client?.email ?: "", fontSize = 13.sp, color = Color(0xFF9E9E9E))
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(Modifier.weight(1f), "${vm.requests.size}", AppStrings.totalRequests, Color(0xFF1A237E), Color(0xFFE8EAF6))
                    StatCard(Modifier.weight(1f), "${history.count { it.status == "completed" }}", AppStrings.statusCompleted, Color(0xFF2E7D32), Color(0xFFE8F5E9))
                    StatCard(Modifier.weight(1f), if (avgRating > 0) "%.1f ⭐".format(avgRating) else "—", AppStrings.avgRating, Color(0xFFE65100), Color(0xFFFFF3E0))
                }
            }

            // Active requests
            if (active.isNotEmpty()) {
                item {
                    Text("${AppStrings.activeRequests} (${active.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                }
                items(active) { req ->
                    MiniRequestCard(req, purple) { nav.navigate(Screen.ClientRequestDetail.createRoute(req.id)) }
                }
            }

            // History header with delete button
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${AppStrings.clientHistory} (${history.size})",
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242)
                    )
                    if (history.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.clickable { showDeleteConfirm = true }
                        ) {
                            Text(
                                AppStrings.deleteHistoryBtn,
                                fontSize = 12.sp,
                                color = Color(0xFFC62828),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (history.isEmpty()) {
                item {
                    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(AppStrings.noHistoryClient, fontSize = 14.sp, color = Color(0xFF9E9E9E))
                        }
                    }
                }
            } else {
                items(history) { req ->
                    HistoryCard(req, nav)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    // Confirm delete dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title   = { Text(AppStrings.deleteHistoryTitle) },
            text    = { Text(AppStrings.deleteHistoryMsg) },
            confirmButton = {
                Button(
                    onClick = { vm.clearHistory(); showDeleteConfirm = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) { Text(AppStrings.yesDelete) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) { Text(AppStrings.cancelBtn) }
            }
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String, textColor: Color, bg: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = bg), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, fontSize = 11.sp, color = textColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun MiniRequestCard(req: ServiceRequest, purple: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(AppStrings.serviceTypeName(req.serviceType), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = purple)
                Text(req.address, fontSize = 12.sp, color = Color(0xFF9E9E9E))
            }
            val (color, label) = when (req.status) {
                "awaiting_approval" -> Color(0xFF1A237E) to AppStrings.statusDecide
                "accepted"          -> Color(0xFF1565C0) to AppStrings.statusConfirmed
                else                -> Color(0xFFE65100) to AppStrings.pending
            }
            Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.1f)) {
                Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun HistoryCard(req: ServiceRequest, nav: NavController) {
    val (bg, label) = when (req.status) {
        "completed"   -> Color(0xFFE8F5E9) to AppStrings.statusCompleted
        "cancelled"   -> Color(0xFFFFEBEE) to AppStrings.statusCancelled
        else          -> Color(0xFFFFF3E0) to AppStrings.statusNotMatched
    }
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { nav.navigate(Screen.ClientRequestDetail.createRoute(req.id)) },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(AppStrings.serviceTypeName(req.serviceType), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
                Text(label, fontSize = 12.sp, color = Color(0xFF424242))
            }
            if (req.providerName.isNotBlank()) Text("মিস্ত্রি: ${req.providerName}", fontSize = 12.sp, color = Color(0xFF757575))
            if (req.rating > 0) Text("রেটিং: ${"⭐".repeat(req.rating)}", fontSize = 13.sp)
            if (req.reviewComment.isNotBlank()) Text("\"${req.reviewComment}\"", fontSize = 12.sp, color = Color(0xFF424242))
        }
    }
}
