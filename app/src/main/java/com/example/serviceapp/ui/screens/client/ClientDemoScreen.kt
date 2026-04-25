package com.example.serviceapp.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.serviceapp.ui.theme.AppColors

@Composable
fun ClientDemoScreen(nav: NavController) {

    val features = listOf(
        "🔍" to "আপনার এলাকার সেরা মিস্ত্রি খুঁজুন",
        "📅" to "সহজে বুকিং করুন",
        "⭐" to "রেটিং ও রিভিউ দেখুন",
        "💬" to "সরাসরি মিস্ত্রির সাথে কথা বলুন",
        "💳" to "নিরাপদ পেমেন্ট সিস্টেম",
        "🔔" to "রিয়েল-টাইম নোটিফিকেশন পান",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF6A1B9A), Color(0xFF4A148C), Color(0xFF1A237E))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Back button
            IconButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                // Coming soon badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        "🚀  শীঘ্রই আসছে",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    "গ্রাহক অ্যাপ",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "আমরা গ্রাহকদের জন্য সেরা\nঅভিজ্ঞতা তৈরি করছি।\nআর একটু অপেক্ষা করুন!",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(40.dp))

                // Features list
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "আসন্ন ফিচারসমূহ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        features.forEach { (emoji, text) ->
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(emoji, fontSize = 22.sp)
                                Text(text, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                            if (features.last().second != text) {
                                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Back button
                OutlinedButton(
                    onClick = { nav.popBackStack() },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("← ফিরে যান", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
