package com.codex.listenandwrite.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codex.listenandwrite.ui.theme.BorderOutline
import com.codex.listenandwrite.ui.theme.CardBackground
import com.codex.listenandwrite.ui.theme.DividerColor
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.SecondaryText

@Composable
fun LineChartView(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val leftPadding = 32.dp.toPx()
            val rightPadding = 16.dp.toPx()
            val topPadding = 24.dp.toPx()
            val bottomPadding = 24.dp.toPx()

            val chartWidth = w - leftPadding - rightPadding
            val chartHeight = h - topPadding - bottomPadding

            // 1. Draw Grid Lines and Y-Axis Labels
            val yValues = listOf(100 to "100%", 50 to "50%", 0 to "0%")
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 10.dp.toPx()
                textAlign = android.graphics.Paint.Align.RIGHT
            }

            yValues.forEach { (value, label) ->
                val y = topPadding + chartHeight * (1f - (value / 100f))

                // Grid line
                drawLine(
                    color = DividerColor,
                    start = Offset(leftPadding, y),
                    end = Offset(w - rightPadding, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Label
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    leftPadding - 6.dp.toPx(),
                    y + 3.dp.toPx(),
                    paint
                )
            }

            // 2. Compute Points Coordinates
            val stepX = chartWidth / (points.size - 1).coerceAtLeast(1)
            val coords = points.mapIndexed { index, point ->
                val x = leftPadding + index * stepX
                val y = topPadding + chartHeight * (1f - (point.accuracyPercent / 100f))
                Offset(x, y)
            }

            // 3. Draw Gradient Area Fill under the Line
            val fillPath = Path().apply {
                moveTo(coords.first().x, topPadding + chartHeight)
                lineTo(coords.first().x, coords.first().y)
                for (i in 1 until coords.size) {
                    val prev = coords[i - 1]
                    val curr = coords[i]
                    val cx = (prev.x + curr.x) / 2f
                    cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                }
                lineTo(coords.last().x, topPadding + chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryBlue.copy(alpha = 0.25f), Color.Transparent),
                    startY = topPadding,
                    endY = topPadding + chartHeight
                )
            )

            // 4. Draw Smooth Line Path
            val linePath = Path().apply {
                moveTo(coords.first().x, coords.first().y)
                for (i in 1 until coords.size) {
                    val prev = coords[i - 1]
                    val curr = coords[i]
                    val cx = (prev.x + curr.x) / 2f
                    cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                }
            }

            drawPath(
                path = linePath,
                color = PrimaryBlue,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // 5. Draw Dots, Labels, and Highlight Tooltip
            val labelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }

            val tooltipPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#FFFFFF")
                textSize = 10.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }

            coords.forEachIndexed { index, offset ->
                val point = points[index]

                // Point circle
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = offset
                )
                drawCircle(
                    color = PrimaryBlue,
                    radius = 3.dp.toPx(),
                    center = offset
                )

                // Date Label at bottom
                drawContext.canvas.nativeCanvas.drawText(
                    point.dateLabel,
                    offset.x,
                    h - 4.dp.toPx(),
                    labelPaint
                )

                // Highlighted Tooltip matching reference UI ("正确率 85%")
                if (point.isHighlighted) {
                    val tooltipText = "正确率 ${point.accuracyPercent}%"
                    val tooltipWidth = 72.dp.toPx()
                    val tooltipHeight = 22.dp.toPx()
                    val tooltipY = offset.y - tooltipHeight - 6.dp.toPx()

                    drawRoundRect(
                        color = PrimaryBlue,
                        topLeft = Offset(offset.x - tooltipWidth / 2f, tooltipY),
                        size = androidx.compose.ui.geometry.Size(tooltipWidth, tooltipHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        tooltipText,
                        offset.x,
                        tooltipY + 15.dp.toPx(),
                        tooltipPaint
                    )
                }
            }
        }
    }
}
