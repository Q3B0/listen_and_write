package com.codex.listenandwrite.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TrophyIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "trophy_sparkle")
    val sparkleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_scale"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = size.toPx()
            val h = size.toPx()
            val cx = w / 2f
            val cy = h / 2f

            // Golden Colors
            val goldMain = Color(0xFFFFB800)
            val goldDark = Color(0xFFF59E0B)
            val goldLight = Color(0xFFFFE066)

            // 1. Trophy Cup Body
            val cupPath = Path().apply {
                moveTo(cx - 32.dp.toPx(), cy - 24.dp.toPx())
                lineTo(cx + 32.dp.toPx(), cy - 24.dp.toPx())
                cubicTo(
                    cx + 30.dp.toPx(), cy + 16.dp.toPx(),
                    cx + 16.dp.toPx(), cy + 24.dp.toPx(),
                    cx, cy + 24.dp.toPx()
                )
                cubicTo(
                    cx - 16.dp.toPx(), cy + 24.dp.toPx(),
                    cx - 30.dp.toPx(), cy + 16.dp.toPx(),
                    cx - 32.dp.toPx(), cy - 24.dp.toPx()
                )
                close()
            }
            drawPath(cupPath, color = goldMain)

            // Cup Rim Highlight
            drawRoundRect(
                color = goldLight,
                topLeft = Offset(cx - 34.dp.toPx(), cy - 28.dp.toPx()),
                size = Size(68.dp.toPx(), 8.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // 2. Trophy Handles (Left and Right)
            val handleStroke = 5.dp.toPx()
            val leftHandle = Path().apply {
                moveTo(cx - 30.dp.toPx(), cy - 18.dp.toPx())
                cubicTo(
                    cx - 50.dp.toPx(), cy - 18.dp.toPx(),
                    cx - 50.dp.toPx(), cy + 8.dp.toPx(),
                    cx - 24.dp.toPx(), cy + 12.dp.toPx()
                )
            }
            drawPath(leftHandle, color = goldDark, style = Stroke(handleStroke, cap = StrokeCap.Round))

            val rightHandle = Path().apply {
                moveTo(cx + 30.dp.toPx(), cy - 18.dp.toPx())
                cubicTo(
                    cx + 50.dp.toPx(), cy - 18.dp.toPx(),
                    cx + 50.dp.toPx(), cy + 8.dp.toPx(),
                    cx + 24.dp.toPx(), cy + 12.dp.toPx()
                )
            }
            drawPath(rightHandle, color = goldDark, style = Stroke(handleStroke, cap = StrokeCap.Round))

            // 3. Trophy Stem
            drawRect(
                color = goldDark,
                topLeft = Offset(cx - 6.dp.toPx(), cy + 24.dp.toPx()),
                size = Size(12.dp.toPx(), 14.dp.toPx())
            )

            // 4. Trophy Base
            drawRoundRect(
                color = goldMain,
                topLeft = Offset(cx - 24.dp.toPx(), cy + 36.dp.toPx()),
                size = Size(48.dp.toPx(), 12.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // 5. Star on Trophy Cup
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 6.dp.toPx(),
                center = Offset(cx, cy - 2.dp.toPx())
            )

            // 6. Confetti & Sparkles
            val confettiColors = listOf(
                Color(0xFF3B82F6), Color(0xFFEF4444), Color(0xFF10B981),
                Color(0xFFF59E0B), Color(0xFF8B5CF6)
            )

            val sparkleOffsets = listOf(
                Offset(cx - 45.dp.toPx(), cy - 40.dp.toPx()),
                Offset(cx + 45.dp.toPx(), cy - 35.dp.toPx()),
                Offset(cx - 55.dp.toPx(), cy + 10.dp.toPx()),
                Offset(cx + 55.dp.toPx(), cy + 5.dp.toPx()),
                Offset(cx - 20.dp.toPx(), cy - 45.dp.toPx()),
                Offset(cx + 25.dp.toPx(), cy - 48.dp.toPx())
            )

            sparkleOffsets.forEachIndexed { index, offset ->
                drawCircle(
                    color = confettiColors[index % confettiColors.size],
                    radius = (3.dp.toPx() * sparkleScale),
                    center = offset
                )
            }
        }
    }
}
