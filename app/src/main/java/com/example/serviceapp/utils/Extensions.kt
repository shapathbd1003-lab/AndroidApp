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
