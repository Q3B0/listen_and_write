package com.codex.listenandwrite.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

@Composable
fun ChildBannerIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Sky & Sun
        drawCircle(
            color = Color(0xFFFFE082),
            radius = 18.dp.toPx(),
            center = Offset(w * 0.22f, h * 0.28f)
        )

        // 2. Soft Clouds
        val cloudColor = Color.White.copy(alpha = 0.85f)
        drawCircle(cloudColor, radius = 12.dp.toPx(), center = Offset(w * 0.12f, h * 0.22f))
        drawCircle(cloudColor, radius = 16.dp.toPx(), center = Offset(w * 0.16f, h * 0.20f))
        drawCircle(cloudColor, radius = 11.dp.toPx(), center = Offset(w * 0.20f, h * 0.23f))

        // Right Cloud
        drawCircle(cloudColor, radius = 14.dp.toPx(), center = Offset(w * 0.88f, h * 0.30f))
        drawCircle(cloudColor, radius = 18.dp.toPx(), center = Offset(w * 0.93f, h * 0.28f))

        // 3. Green Rolling Hill
        val hillPath = Path().apply {
            moveTo(0f, h * 0.72f)
            cubicTo(
                w * 0.35f, h * 0.60f,
                w * 0.70f, h * 0.68f,
                w, h * 0.62f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hillPath, color = Color(0xFF66BB6A))

        // Secondary hill layer
        val frontHill = Path().apply {
            moveTo(0f, h * 0.82f)
            cubicTo(
                w * 0.45f, h * 0.76f,
                w * 0.75f, h * 0.84f,
                w, h * 0.75f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(frontHill, color = Color(0xFF4CAF50))

        // 4. Cute Kid reading book (right-center positioned)
        val kidCenterX = w * 0.74f
        val kidCenterY = h * 0.58f

        // Body / Shirt (Blue #2388F8)
        val shirtPath = Path().apply {
            moveTo(kidCenterX - 18.dp.toPx(), kidCenterY + 16.dp.toPx())
            lineTo(kidCenterX + 18.dp.toPx(), kidCenterY + 16.dp.toPx())
            lineTo(kidCenterX + 22.dp.toPx(), kidCenterY + 40.dp.toPx())
            lineTo(kidCenterX - 22.dp.toPx(), kidCenterY + 40.dp.toPx())
            close()
        }
        drawPath(shirtPath, color = Color(0xFF2388F8))

        // Head (Skin tone)
        val skinColor = Color(0xFFFFDCB5)
        drawCircle(
            color = skinColor,
            radius = 18.dp.toPx(),
            center = Offset(kidCenterX, kidCenterY - 4.dp.toPx())
        )

        // Hair (Warm Brown)
        val hairPath = Path().apply {
            moveTo(kidCenterX - 19.dp.toPx(), kidCenterY - 4.dp.toPx())
            cubicTo(
                kidCenterX - 18.dp.toPx(), kidCenterY - 26.dp.toPx(),
                kidCenterX + 18.dp.toPx(), kidCenterY - 26.dp.toPx(),
                kidCenterX + 19.dp.toPx(), kidCenterY - 4.dp.toPx()
            )
            cubicTo(
                kidCenterX + 12.dp.toPx(), kidCenterY - 14.dp.toPx(),
                kidCenterX - 12.dp.toPx(), kidCenterY - 14.dp.toPx(),
                kidCenterX - 19.dp.toPx(), kidCenterY - 4.dp.toPx()
            )
            close()
        }
        drawPath(hairPath, color = Color(0xFF5D4037))

        // Cheerful Eyes & Smile
        drawCircle(Color(0xFF333333), radius = 2.dp.toPx(), center = Offset(kidCenterX - 6.dp.toPx(), kidCenterY - 4.dp.toPx()))
        drawCircle(Color(0xFF333333), radius = 2.dp.toPx(), center = Offset(kidCenterX + 6.dp.toPx(), kidCenterY - 4.dp.toPx()))
        // Rosy cheeks
        drawCircle(Color(0xFFFF8A80).copy(alpha = 0.6f), radius = 3.dp.toPx(), center = Offset(kidCenterX - 10.dp.toPx(), kidCenterY + 2.dp.toPx()))
        drawCircle(Color(0xFFFF8A80).copy(alpha = 0.6f), radius = 3.dp.toPx(), center = Offset(kidCenterX + 10.dp.toPx(), kidCenterY + 2.dp.toPx()))

        // Open Book in child's hands
        val bookY = kidCenterY + 30.dp.toPx()
        // Left Page (White)
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(kidCenterX - 20.dp.toPx(), bookY),
            size = Size(18.dp.toPx(), 14.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        // Right Page (White)
        drawRoundRect(
            color = Color(0xFFF5F5F5),
            topLeft = Offset(kidCenterX + 2.dp.toPx(), bookY),
            size = Size(18.dp.toPx(), 14.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        // Book Cover Edge (Warm Orange/Red)
        drawRect(
            color = Color(0xFFFF7043),
            topLeft = Offset(kidCenterX - 21.dp.toPx(), bookY + 12.dp.toPx()),
            size = Size(42.dp.toPx(), 3.dp.toPx())
        )
    }
}
