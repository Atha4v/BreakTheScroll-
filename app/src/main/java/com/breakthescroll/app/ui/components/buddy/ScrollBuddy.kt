package com.breakthescroll.app.ui.components.buddy

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.BuddyCream
import com.breakthescroll.app.ui.theme.BuddyFox
import com.breakthescroll.app.ui.theme.DeepSlate
import com.breakthescroll.app.ui.theme.ObsidianElevated
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.WarmAmber

fun buddyPromptFor(mood: BuddyMood): String = when (mood) {
    BuddyMood.Idle -> "Chalo, ek chhoti si win pakadte hain ✨"
    BuddyMood.Cheer -> "Yesss! Mast kiya — scroll thoda wait karega 🦊"
    BuddyMood.Rest -> "Main yahin hoon. Shaant raho, phone baad mein."
    BuddyMood.Nudge -> "Arre, feed chhod ke real world try karo na?"
}

@Composable
fun ScrollBuddyBanner(
    mood: BuddyMood,
    modifier: Modifier = Modifier,
    customMessage: String? = null,
) {
    val message = customMessage ?: buddyPromptFor(mood)
    val bounce = rememberInfiniteTransition(label = "buddyBounce")
    val floatY by bounce.animateFloat(
        initialValue = 0f,
        targetValue = if (mood == BuddyMood.Cheer) -10f else -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "buddyY",
    )
    val earWiggle by bounce.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ear",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianElevated, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    translationY = floatY
                    rotationZ = if (mood == BuddyMood.Cheer) earWiggle * 0.4f else 0f
                },
            contentAlignment = Alignment.Center,
        ) {
            FoxBuddyFace(mood = mood, earTilt = earWiggle)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Buddy",
                style = MaterialTheme.typography.labelLarge,
                color = WarmAmber,
            )
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedContent(
                targetState = message,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                label = "buddySpeech",
            ) { text ->
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .background(DeepSlate, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftFrost,
                    )
                }
            }
        }
    }
}

@Composable
private fun FoxBuddyFace(
    mood: BuddyMood,
    earTilt: Float,
) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val w = size.width
        val h = size.height

        // Ears
        val leftEar = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.22f, h * 0.42f)
            lineTo(w * 0.18f, h * 0.08f)
            lineTo(w * 0.4f, h * 0.28f)
            close()
        }
        val rightEar = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.78f, h * 0.42f)
            lineTo(w * 0.82f, h * 0.08f)
            lineTo(w * 0.6f, h * 0.28f)
            close()
        }
        drawPath(leftEar, BuddyFox)
        drawPath(rightEar, BuddyFox)

        // Head
        drawRoundRect(
            color = BuddyFox,
            topLeft = Offset(w * 0.16f, h * 0.22f),
            size = Size(w * 0.68f, h * 0.62f),
            cornerRadius = CornerRadius(28f, 28f),
        )
        // Snout
        drawRoundRect(
            color = BuddyCream,
            topLeft = Offset(w * 0.32f, h * 0.52f),
            size = Size(w * 0.36f, h * 0.24f),
            cornerRadius = CornerRadius(18f, 18f),
        )
        drawCircle(DeepSlate, w * 0.035f, Offset(w * 0.5f, h * 0.62f))

        // Eyes
        when (mood) {
            BuddyMood.Cheer -> {
                drawLine(DeepSlate, Offset(w * 0.34f, h * 0.42f), Offset(w * 0.42f, h * 0.46f), 4f)
                drawLine(DeepSlate, Offset(w * 0.42f, h * 0.46f), Offset(w * 0.34f, h * 0.5f), 4f)
                drawLine(DeepSlate, Offset(w * 0.58f, h * 0.46f), Offset(w * 0.66f, h * 0.42f), 4f)
                drawLine(DeepSlate, Offset(w * 0.58f, h * 0.46f), Offset(w * 0.66f, h * 0.5f), 4f)
            }
            BuddyMood.Rest -> {
                drawLine(DeepSlate, Offset(w * 0.32f, h * 0.46f), Offset(w * 0.42f, h * 0.46f), 4f)
                drawLine(DeepSlate, Offset(w * 0.58f, h * 0.46f), Offset(w * 0.68f, h * 0.46f), 4f)
            }
            else -> {
                drawCircle(DeepSlate, w * 0.035f, Offset(w * 0.37f, h * 0.45f))
                drawCircle(DeepSlate, w * 0.035f, Offset(w * 0.63f, h * 0.45f))
                if (mood == BuddyMood.Nudge) {
                    drawCircle(SereneMint, w * 0.012f, Offset(w * 0.38f, h * 0.44f))
                    drawCircle(SereneMint, w * 0.012f, Offset(w * 0.64f, h * 0.44f))
                }
            }
        }

        // Cheek blush
        drawCircle(ColorBlush, w * 0.04f, Offset(w * 0.26f, h * 0.55f))
        drawCircle(ColorBlush, w * 0.04f, Offset(w * 0.74f, h * 0.55f))

        // Subtle ear tilt cue via tiny highlight
        drawCircle(
            BuddyCream.copy(alpha = 0.35f),
            w * 0.03f,
            Offset(w * (0.28f + earTilt * 0.002f), h * 0.2f),
        )
    }
}

private val ColorBlush = Color(0x55FF8A65)
