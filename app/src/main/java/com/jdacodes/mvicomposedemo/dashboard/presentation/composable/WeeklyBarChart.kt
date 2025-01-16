package com.jdacodes.mvicomposedemo.dashboard.presentation.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jdacodes.mvicomposedemo.dashboard.presentation.DailyFrequency

@Composable
fun WeeklyBarChart(
    data: List<DailyFrequency>, // Data: List of DailyFrequency objects
    barColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    chartHeight: Dp = 200.dp,
    showDayLabels: Boolean = true,
    showValueLabels: Boolean = true
) {
    val density = LocalDensity.current
    val dataMap = data.associate { it.day to it.frequency }
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dataWithZero: Map<String, Int> = daysOfWeek.associateWith { dataMap[it] ?: 0 }
    val maxValue = dataWithZero.values.maxOrNull() ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (maxValue > 0) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width / (daysOfWeek.size * 1.5f)
                val barSpacing = barWidth / 2f
                val availableHeight = size.height

                dataWithZero.entries.forEachIndexed { index, (day, value) ->
                    val barHeight =
                        if (maxValue == 0) 0f else (value.toFloat() / maxValue.toFloat()) * availableHeight
                    val xPos = (barWidth + barSpacing) * index + barSpacing
                    val barTop = availableHeight - barHeight

                    // Draw bar
                    drawRect(
                        color = barColor,
                        topLeft = Offset(xPos, barTop),
                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                    )

                    // Draw value label
                    if (showValueLabels) {
                        drawContext.canvas.nativeCanvas.apply {
                            val labelPaint = android.graphics.Paint().apply {
                                color = textColor.toArgb()
                                textSize = with(density) { 12.sp.toPx() }
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText(
                                value.toString(),
                                xPos + barWidth / 2,
                                barTop - 8.dp.toPx(),
                                labelPaint
                            )
                        }
                    }

                    // Draw day label
                    if (showDayLabels) {
                        drawContext.canvas.nativeCanvas.apply {
                            val labelPaint = android.graphics.Paint().apply {
                                color = textColor.toArgb()
                                textSize = with(density) { 10.sp.toPx() }
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText(
                                day,
                                xPos + barWidth / 2,
                                availableHeight + 16.dp.toPx(),
                                labelPaint
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No Data Available",
                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
