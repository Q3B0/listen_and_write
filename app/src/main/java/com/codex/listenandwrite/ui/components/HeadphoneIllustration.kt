package com.codex.listenandwrite.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codex.listenandwrite.ui.theme.LightBlueBg
import com.codex.listenandwrite.ui.theme.PrimaryBlue
import com.codex.listenandwrite.ui.theme.PrimaryDarkBlue

@Composable
fun HeadphoneIllustration(
    modifier: Modifier = Modifier,
    illustrationSize: Dp = 220.dp,
    isSpeaking: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "soundwave_transition")

    val waveAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isSpeaking) 0.9f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_alpha_1"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isSpeaking) 6f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_offset"
    )

    Box(
        modifier = modifier
            .size(illustrationSize)
            .clip(CircleShape)
            .background(LightBlueBg),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(illustrationSize * 0.75f)) {
            val cWidth = size.width
            val cHeight = size.height
            val centerX = cWidth / 2f
            val centerY = cHeight / 2f

            // 1. Draw Soundwaves on the left and right if active
            if (isSpeaking) {
                // Left sound waves
                val leftBaseX = centerX - 58.dp.toPx()
                for (i in 1..2) {
                    val arcRadius = (16 + i * 14).dp.toPx()
                    val shift = i.toFloat() * waveOffset
                    val leftWave = Path().apply {
                        arcTo(
                            rect = Rect(
                                left = leftBaseX - arcRadius - shift,
                                top = centerY - arcRadius,
                                right = leftBaseX + arcRadius - shift,
                                bottom = centerY + arcRadius
                            ),
                            startAngleDegrees = 120f,
                            sweepAngleDegrees = 120f,
                            forceMoveTo = true
                        )
                    }
                    drawPath(
                        path = leftWave,
                        color = PrimaryBlue.copy(alpha = waveAlpha1 / i.toFloat()),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Right sound waves
                val rightBaseX = centerX + 58.dp.toPx()
                for (i in 1..2) {
                    val arcRadius = (16 + i * 14).dp.toPx()
                    val shift = i.toFloat() * waveOffset
                    val rightWave = Path().apply {
                        arcTo(
                            rect = Rect(
                                left = rightBaseX - arcRadius + shift,
                                top = centerY - arcRadius,
                                right = rightBaseX + arcRadius + shift,
                                bottom = centerY + arcRadius
                            ),
                            startAngleDegrees = -60f,
                            sweepAngleDegrees = 120f,
                            forceMoveTo = true
                        )
                    }
                    drawPath(
                        path = rightWave,
                        color = PrimaryBlue.copy(alpha = waveAlpha1 / i.toFloat()),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // 2. Headband Arc
            val headBandRadius = 40.dp.toPx()
            val headbandPath = Path().apply {
                arcTo(
                    rect = Rect(
                        left = centerX - headBandRadius,
                        top = centerY - headBandRadius - 6.dp.toPx(),
                        right = centerX + headBandRadius,
                        bottom = centerY + headBandRadius - 6.dp.toPx()
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = true
                )
            }
            drawPath(
                path = headbandPath,
                color = PrimaryBlue,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Left Ear Pad
            val earpadWidth = 18.dp.toPx()
            val earpadHeight = 44.dp.toPx()
            val leftEarpadX = centerX - headBandRadius - (earpadWidth / 2f)
            val earpadY = centerY - 10.dp.toPx()

            drawRoundRect(
                color = PrimaryDarkBlue,
                topLeft = Offset(leftEarpadX, earpadY),
                size = Size(earpadWidth, earpadHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Inner cushion accent
            drawRoundRect(
                color = PrimaryBlue,
                topLeft = Offset(leftEarpadX + 3.dp.toPx(), earpadY + 4.dp.toPx()),
                size = Size(earpadWidth - 6.dp.toPx(), earpadHeight - 8.dp.toPx()),
                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )

            // 4. Right Ear Pad
            val rightEarpadX = centerX + headBandRadius - (earpadWidth / 2f)
            drawRoundRect(
                color = PrimaryDarkBlue,
                topLeft = Offset(rightEarpadX, earpadY),
                size = Size(earpadWidth, earpadHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Inner cushion accent
            drawRoundRect(
                color = PrimaryBlue,
                topLeft = Offset(rightEarpadX + 3.dp.toPx(), earpadY + 4.dp.toPx()),
                size = Size(earpadWidth - 6.dp.toPx(), earpadHeight - 8.dp.toPx()),
                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
        }
    }
}
