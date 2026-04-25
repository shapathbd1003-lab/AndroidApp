package com.example.serviceapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.serviceapp.viewmodel.ClientViewModel

@Composable
fun ClientDashboardScreen(vm: ClientViewModel, nav: NavController) {
    val purple = Color(0xFF6A1B9A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF9C27B0))))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Text("স্বাগতম,", fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
                Text(vm.client?.name ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            IconButton(
                onClick = { vm.logout(); nav.navigate(Screen.RoleSelection.route) { popUpTo(0) { inclusive = true } } },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Color.White)
            }
        }

        // Content
        if (vm.requests.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔧", fontSize = 56.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("এখনো কোনো অনুরোধ নেই", fontSize = 16.sp, color = Color(0xFF757575))
                    Text("নিচের বাটনে চাপ দিয়ে সেবা নিন", fontSize = 13.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.requests) { req ->
                    RequestCard(req) {
                        nav.navigate(Screen.ClientRequestDetail.createRoute(req.id))
                    }
                }
            }
        }

        // New request button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Button(
                onClick = { nav.navigate(Screen.ClientNewRequest.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purple)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("নতুন সেবার অনুরোধ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RequestCard(req: ServiceRequest, onClick: () -> Unit) {
    val (statusColor, statusLabel) = when (req.status) {
        "accepted"  -> Color(0xFF1565C0) to "✅ মিস্ত্রি নিশ্চিত করেছেন"
        "completed" -> Color(0xFF2E7D32) to "☑️ সম্পন্ন"
        else        -> Color(0xFFE65100) to "⏳ অপেক্ষমান..."
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(req.serviceType, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.12f)) {
                    Text(statusLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(req.description, fontSize = 13.sp, color = Color(0xFF757575), maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text("📍 ${req.address}", fontSize = 12.sp, color = Color(0xFF9E9E9E))
            if (req.status == "accepted" && req.providerName.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("মিস্ত্রি: ${req.providerName} • ${req.providerPhone}", fontSize = 12.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold)
            }
            if (req.status == "completed" && req.rating > 0) {
                Spacer(Modifier.height(6.dp))
                Text("রেটিং: ${"⭐".repeat(req.rating)}", fontSize = 13.sp)
            }
        }
    }
}
