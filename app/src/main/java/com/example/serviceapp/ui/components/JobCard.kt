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
fun JobCard(
    job:            Job,
    onAccept:       () -> Unit,
    onClick:        () -> Unit,
    onMarkOnTheWay: (() -> Unit)? = null,
    onMarkArrived:  (() -> Unit)? = null,
    onMarkWorking:  (() -> Unit)? = null,
    onMarkFinished: (() -> Unit)? = null,
    hasPoints:      Boolean = true
) {

    val isDone      = job.status == "done"
    val isAwaiting  = job.status == "awaiting"
    val isAgreed    = job.status == "agreed"
    val isOnTheWay  = job.status == "on_the_way"
    val isArrived   = job.status == "arrived"
    val isWorking   = job.status == "working"

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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Problem type badge
                    val (ptColor, ptBg) = when (job.problemType) {
                        "critical" -> AppColors.Error       to Color(0xFFFFEBEE)
                        "advanced" -> AppColors.Primary     to AppColors.PrimaryContainer
                        else       -> Color(0xFF2E7D32)     to Color(0xFFE8F5E9)
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = ptBg) {
                        Text(
                            AppStrings.problemTypeName(job.problemType),
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = ptColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    // Status badge
                    val (statusBg, statusFg, statusLabel) = when (job.status) {
                        "done"       -> Triple(Color(0xFFE8F5E9), AppColors.Success,     AppStrings.finishedStatus)
                        "awaiting"   -> Triple(Color(0xFFFFF8E1), Color(0xFFE65100),     AppStrings.awaitingClientApproval)
                        "agreed"     -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0),     AppStrings.clientAgreed)
                        "on_the_way" -> Triple(Color(0xFFE8EAF6), AppColors.Primary,     AppStrings.onTheWayStatus)
                        "arrived"    -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32),     AppStrings.arrivedStatus)
                        "working"    -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100),     AppStrings.workingStatus)
                        else         -> Triple(AppColors.PrimaryContainer, AppColors.Primary, AppStrings.pending)
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = statusBg) {
                        Text(
                            statusLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusFg,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
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
                Text(job.address, fontSize = 12.sp, color = AppColors.TextSecondary, modifier = Modifier.weight(1f))
                if (job.distanceKm >= 0) {
                    Spacer(Modifier.width(8.dp))
                    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp), color = AppColors.PrimaryContainer) {
                        Text(
                            if (job.distanceKm < 1.0) "${"%.0f".format(job.distanceKm * 1000)} m"
                            else "${"%.1f".format(job.distanceKm)} km",
                            fontSize = 11.sp, color = AppColors.Primary, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                if (!isDone && !isAwaiting && !isAgreed && !isOnTheWay) {
                    if (!hasPoints) {
                        // Insufficient points warning
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFFFEBEE)) {
                            Text(AppStrings.insufficientPoints, fontSize = 12.sp, color = Color(0xFFC62828),
                                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                        }
                    } else {
                        Button(
                            onClick = { onAccept() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(AppStrings.accept, fontSize = 13.sp)
                        }
                    }
                } else if (isAgreed && onMarkOnTheWay != null) {
                    Button(onClick = { onMarkOnTheWay() }, shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                        Text(AppStrings.markOnTheWay, fontSize = 13.sp)
                    }
                } else if (isOnTheWay && onMarkArrived != null) {
                    Button(onClick = { onMarkArrived() }, shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                        Text(AppStrings.markArrived, fontSize = 13.sp)
                    }
                } else if (isArrived && onMarkWorking != null) {
                    Button(onClick = { onMarkWorking() }, shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))) {
                        Text(AppStrings.markWorking, fontSize = 13.sp)
                    }
                } else if (isWorking && onMarkFinished != null) {
                    Button(onClick = { onMarkFinished() }, shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                        Text(AppStrings.markFinished, fontSize = 13.sp)
                    }
                } else if (isDone) {
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
