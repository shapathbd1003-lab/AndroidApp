package com.example.serviceapp.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Double.toBDT(): String = "৳ ${"%.0f".format(this)}"

/** Returns "Today, HH:mm" / "Yesterday, HH:mm" / "dd MMM, HH:mm" */
fun formatTimestamp(epochSeconds: Long): String {
    val date  = java.util.Date(epochSeconds * 1000)
    val cal   = java.util.Calendar.getInstance().apply { time = date }
    val today = java.util.Calendar.getInstance()
    val yest  = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }
    val timeFmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.ENGLISH)
    return when {
        cal[java.util.Calendar.YEAR] == today[java.util.Calendar.YEAR] &&
        cal[java.util.Calendar.DAY_OF_YEAR] == today[java.util.Calendar.DAY_OF_YEAR] ->
            "Today, ${timeFmt.format(date)}"
        cal[java.util.Calendar.YEAR] == yest[java.util.Calendar.YEAR] &&
        cal[java.util.Calendar.DAY_OF_YEAR] == yest[java.util.Calendar.DAY_OF_YEAR] ->
            "Yesterday, ${timeFmt.format(date)}"
        else ->
            java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.ENGLISH).format(date)
    }
}

fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    width: Dp = 3.dp,
    color: Color = Color(0x55000000)
): Modifier = drawWithContent {
    drawContent()
    val maxScroll = scrollState.maxValue.toFloat()
    if (maxScroll <= 0f) return@drawWithContent
    val viewportH   = size.height
    val fraction    = viewportH / (viewportH + maxScroll)
    val thumbH      = viewportH * fraction
    val thumbY      = (scrollState.value.toFloat() / maxScroll) * (viewportH - thumbH)
    drawRoundRect(
        color       = color,
        topLeft     = Offset(size.width - width.toPx() - 2.dp.toPx(), thumbY),
        size        = Size(width.toPx(), thumbH),
        cornerRadius = CornerRadius(width.toPx() / 2f)
    )
}
