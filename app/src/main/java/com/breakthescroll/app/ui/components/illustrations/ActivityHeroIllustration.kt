package com.breakthescroll.app.ui.components.illustrations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.breakthescroll.app.ui.model.ActivityIllustration
import com.breakthescroll.app.ui.theme.SoftFrost

@Composable
fun ActivityHeroIllustration(
    kind: ActivityIllustration,
    heroColors: List<Color>,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.linearGradient(
        colors = if (heroColors.size >= 2) heroColors else listOf(heroColors.first(), heroColors.first()),
    )

    Box(
        modifier = modifier.background(gradient),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = SoftFrost.copy(alpha = 0.08f),
                radius = w * 0.28f,
                center = Offset(w * 0.18f, h * 0.25f),
            )
            drawCircle(
                color = SoftFrost.copy(alpha = 0.06f),
                radius = w * 0.22f,
                center = Offset(w * 0.82f, h * 0.7f),
            )

            when (kind) {
                ActivityIllustration.Walk -> {
                    drawRoundRect(
                        color = SoftFrost.copy(alpha = 0.18f),
                        topLeft = Offset(w * 0.22f, h * 0.62f),
                        size = Size(w * 0.56f, h * 0.08f),
                        cornerRadius = CornerRadius(24f, 24f),
                    )
                    drawCircle(SoftFrost.copy(alpha = 0.9f), w * 0.06f, Offset(w * 0.5f, h * 0.28f))
                    drawLine(SoftFrost.copy(alpha = 0.85f), Offset(w * 0.5f, h * 0.34f), Offset(w * 0.5f, h * 0.52f), 8f)
                    drawLine(SoftFrost.copy(alpha = 0.85f), Offset(w * 0.5f, h * 0.4f), Offset(w * 0.36f, h * 0.5f), 7f)
                    drawLine(SoftFrost.copy(alpha = 0.85f), Offset(w * 0.5f, h * 0.4f), Offset(w * 0.64f, h * 0.48f), 7f)
                    drawLine(SoftFrost.copy(alpha = 0.85f), Offset(w * 0.5f, h * 0.52f), Offset(w * 0.38f, h * 0.66f), 7f)
                    drawLine(SoftFrost.copy(alpha = 0.85f), Offset(w * 0.5f, h * 0.52f), Offset(w * 0.62f, h * 0.66f), 7f)
                }

                ActivityIllustration.Bike -> {
                    drawCircle(SoftFrost.copy(alpha = 0.85f), w * 0.11f, Offset(w * 0.32f, h * 0.62f), style = Stroke(8f))
                    drawCircle(SoftFrost.copy(alpha = 0.85f), w * 0.11f, Offset(w * 0.68f, h * 0.62f), style = Stroke(8f))
                    val frame = Path().apply {
                        moveTo(w * 0.32f, h * 0.62f)
                        lineTo(w * 0.48f, h * 0.42f)
                        lineTo(w * 0.62f, h * 0.42f)
                        lineTo(w * 0.68f, h * 0.62f)
                        moveTo(w * 0.48f, h * 0.42f)
                        lineTo(w * 0.5f, h * 0.62f)
                    }
                    drawPath(frame, SoftFrost.copy(alpha = 0.9f), style = Stroke(7f))
                    drawLine(SoftFrost.copy(alpha = 0.9f), Offset(w * 0.62f, h * 0.42f), Offset(w * 0.58f, h * 0.34f), 6f)
                }

                ActivityIllustration.Yoga -> {
                    drawCircle(SoftFrost.copy(alpha = 0.9f), w * 0.055f, Offset(w * 0.5f, h * 0.28f))
                    drawLine(SoftFrost.copy(alpha = 0.9f), Offset(w * 0.5f, h * 0.34f), Offset(w * 0.5f, h * 0.5f), 8f)
                    drawLine(SoftFrost.copy(alpha = 0.9f), Offset(w * 0.32f, h * 0.42f), Offset(w * 0.68f, h * 0.42f), 7f)
                    drawLine(SoftFrost.copy(alpha = 0.9f), Offset(w * 0.5f, h * 0.5f), Offset(w * 0.34f, h * 0.68f), 7f)
                    drawLine(SoftFrost.copy(alpha = 0.9f), Offset(w * 0.5f, h * 0.5f), Offset(w * 0.66f, h * 0.68f), 7f)
                }

                ActivityIllustration.Breath -> {
                    drawCircle(SoftFrost.copy(alpha = 0.15f), w * 0.28f, Offset(w * 0.5f, h * 0.48f))
                    drawCircle(SoftFrost.copy(alpha = 0.25f), w * 0.18f, Offset(w * 0.5f, h * 0.48f))
                    drawCircle(SoftFrost.copy(alpha = 0.85f), w * 0.08f, Offset(w * 0.5f, h * 0.48f))
                }

                ActivityIllustration.Hydrate -> {
                    val glass = Path().apply {
                        moveTo(w * 0.38f, h * 0.28f)
                        lineTo(w * 0.34f, h * 0.72f)
                        lineTo(w * 0.66f, h * 0.72f)
                        lineTo(w * 0.62f, h * 0.28f)
                        close()
                    }
                    drawPath(glass, SoftFrost.copy(alpha = 0.2f))
                    drawPath(glass, SoftFrost.copy(alpha = 0.85f), style = Stroke(6f))
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.45f),
                        topLeft = Offset(w * 0.37f, h * 0.48f),
                        size = Size(w * 0.26f, h * 0.22f),
                        cornerRadius = CornerRadius(8f, 8f),
                    )
                }

                ActivityIllustration.Read -> {
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.9f),
                        topLeft = Offset(w * 0.28f, h * 0.3f),
                        size = Size(w * 0.2f, h * 0.4f),
                        cornerRadius = CornerRadius(10f, 10f),
                    )
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.75f),
                        topLeft = Offset(w * 0.5f, h * 0.3f),
                        size = Size(w * 0.2f, h * 0.4f),
                        cornerRadius = CornerRadius(10f, 10f),
                    )
                    drawLine(SoftFrost.copy(alpha = 0.5f), Offset(w * 0.5f, h * 0.3f), Offset(w * 0.5f, h * 0.7f), 4f)
                }

                ActivityIllustration.Tea -> {
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.85f),
                        topLeft = Offset(w * 0.36f, h * 0.38f),
                        size = Size(w * 0.28f, h * 0.28f),
                        cornerRadius = CornerRadius(18f, 18f),
                    )
                    drawArc(
                        SoftFrost.copy(alpha = 0.85f),
                        startAngle = -60f,
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset(w * 0.58f, h * 0.42f),
                        size = Size(w * 0.16f, h * 0.18f),
                        style = Stroke(6f),
                    )
                    drawLine(SoftFrost.copy(alpha = 0.55f), Offset(w * 0.44f, h * 0.28f), Offset(w * 0.44f, h * 0.36f), 4f)
                    drawLine(SoftFrost.copy(alpha = 0.55f), Offset(w * 0.52f, h * 0.26f), Offset(w * 0.52f, h * 0.36f), 4f)
                }

                ActivityIllustration.PhoneDown -> {
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.9f),
                        topLeft = Offset(w * 0.38f, h * 0.28f),
                        size = Size(w * 0.24f, h * 0.42f),
                        cornerRadius = CornerRadius(22f, 22f),
                    )
                    drawCircle(SoftFrost.copy(alpha = 0.35f), w * 0.02f, Offset(w * 0.5f, h * 0.64f))
                    drawLine(SoftFrost.copy(alpha = 0.7f), Offset(w * 0.3f, h * 0.72f), Offset(w * 0.7f, h * 0.34f), 6f)
                }

                ActivityIllustration.Social -> {
                    drawCircle(SoftFrost.copy(alpha = 0.9f), w * 0.08f, Offset(w * 0.38f, h * 0.4f))
                    drawCircle(SoftFrost.copy(alpha = 0.75f), w * 0.08f, Offset(w * 0.62f, h * 0.4f))
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.25f),
                        topLeft = Offset(w * 0.28f, h * 0.55f),
                        size = Size(w * 0.44f, h * 0.18f),
                        cornerRadius = CornerRadius(28f, 28f),
                    )
                }

                ActivityIllustration.Window -> {
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.2f),
                        topLeft = Offset(w * 0.26f, h * 0.22f),
                        size = Size(w * 0.48f, h * 0.56f),
                        cornerRadius = CornerRadius(16f, 16f),
                    )
                    drawLine(SoftFrost.copy(alpha = 0.85f), Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.78f), 5f)
                    drawLine(SoftFrost.copy(alpha = 0.85f), Offset(w * 0.26f, h * 0.5f), Offset(w * 0.74f, h * 0.5f), 5f)
                    drawRoundRect(
                        SoftFrost.copy(alpha = 0.85f),
                        topLeft = Offset(w * 0.26f, h * 0.22f),
                        size = Size(w * 0.48f, h * 0.56f),
                        cornerRadius = CornerRadius(16f, 16f),
                        style = Stroke(6f),
                    )
                }
            }
        }
    }
}
