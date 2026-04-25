package com.example.serviceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.serviceapp.data.model.Job
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings

@Composable
fun JobCard(job: Job, onAccept: () -> Unit, onClick: () -> Unit) {

    val isDone = job.status == "done"

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    job.description,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDone) Color(0xFFE8F5E9) else AppColors.PrimaryContainer
                ) {
                    Text(
                        if (isDone) AppStrings.accepted else AppStrings.pending,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDone) AppColors.Success else AppColors.Primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (job.overview.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(AppColors.Background, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        job.overview,
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text(job.address, fontSize = 12.sp, color = AppColors.TextSecondary)
                Spacer(Modifier.width(14.dp))
                Icon(Icons.Default.Phone, null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text(job.phone, fontSize = 12.sp, color = AppColors.TextSecondary)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isDone) {
                    Button(
                        onClick = { onAccept() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(AppStrings.accept, fontSize = 13.sp)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, null, tint = AppColors.Success, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(AppStrings.accepted, fontSize = 13.sp, color = AppColors.Success, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
