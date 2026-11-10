package com.breakthescroll.app.ui.components.streak

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import kotlin.math.sin

@Composable
fun StreakFireBadge(
    streak: Int,
    longest: Int,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val motion = rememberInfiniteTransition(label = "ember")
    val flicker by motion.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flicker",
    )
    val drift by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "drift",
    )
    val multiplier = when {
        streak >= 30 -> "×3.0 legend"
        streak >= 14 -> "×2.0 heat"
        streak >= 7 -> "×1.5 warm"
        streak >= 3 -> "×1.2 lit"
        else -> "×1.0 spark"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF7C2D12),
                        Color(0xFFEA580C),
                        Color(0xFFFBBF24),
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(26.dp)),
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = 0.55f },
        ) {
            val w = size.width
            val h = size.height
            repeat(10) { i ->
                val x = (w * ((i * 0.11f + drift * 0.35f) % 1f))
                val y = h * (0.75f - ((drift + i * 0.08f) % 1f) * 0.7f)
                val radius = 3.5f + (i % 3) * 1.8f
                drawCircle(
                    color = Color(0xFFFFF7ED).copy(alpha = 0.35f + 0.08f * sin(i + drift * 6f)),
                    radius = radius * flicker,
                    center = Offset(x, y),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "🔥",
                fontSize = 42.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = flicker
                    scaleY = flicker
                    translationY = (1f - flicker) * 8f
                },
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$streak-day streak",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
                Text(
                    text = "Best · $longest days  ·  $multiplier",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f),
                )
                Spacer(modifier = Modifier.height(10.dp))
                MilestoneHeatRow(streak = streak)
            }
        }
    }
}

@Composable
private fun MilestoneHeatRow(streak: Int) {
    val milestones = listOf(1, 3, 7, 14, 30)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        milestones.forEach { day ->
            val lit = streak >= day
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(
                        if (lit) Color.White.copy(alpha = 0.95f)
                        else Color.Black.copy(alpha = 0.25f),
                    ),
            )
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "Milestones 1 · 3 · 7 · 14 · 30",
        style = MaterialTheme.typography.labelMedium,
        color = Color.White.copy(alpha = 0.75f),
    )
}
