package com.breakthescroll.app.ui.components.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.ui.theme.LocalBtsPalette

@Composable
fun PulsingStartFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val pulse = rememberInfiniteTransition(label = "startPulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseScale",
    )
    val alpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulseAlpha",
    )

    Box(
        modifier = modifier.size(74.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(74.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
        ) {
            drawCircle(color = palette.primary)
        }
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = palette.primary,
            contentColor = palette.onPrimary,
            modifier = Modifier.size(58.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = "START reset",
            )
        }
    }
}
