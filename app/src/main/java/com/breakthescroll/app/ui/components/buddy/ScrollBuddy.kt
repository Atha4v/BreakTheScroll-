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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.BuddyBadgerCream
import com.breakthescroll.app.ui.theme.BuddyBadgerFur
import com.breakthescroll.app.ui.theme.BuddyBadgerStripe
import com.breakthescroll.app.ui.theme.LocalBtsPalette

fun buddyPromptFor(mood: BuddyMood): String = when (mood) {
    BuddyMood.Idle -> "Honey badger mode on. Chalo, ek chhoti win pakadte hain."
    BuddyMood.Working -> "Main saath hoon — focus tight, scroll weak."
    BuddyMood.Cheer -> "That's persistence! Never give up — mast kiya 🦡"
    BuddyMood.Rest -> "Rest bhi strength hai. Phone baad mein."
    BuddyMood.Nudge -> "Feed chor, real world try kar. Badger doesn't quit."
}

@Composable
fun ScrollBuddyBanner(
    mood: BuddyMood,
    modifier: Modifier = Modifier,
    customMessage: String? = null,
) {
    val palette = LocalBtsPalette.current
    val message = customMessage ?: buddyPromptFor(mood)
    val bounce = rememberInfiniteTransition(label = "badgerBounce")
    val floatY by bounce.animateFloat(
        initialValue = 0f,
        targetValue = when (mood) {
            BuddyMood.Cheer -> -10f
            BuddyMood.Working -> -3f
            else -> -5f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "badgerY",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.surfaceElevated, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .graphicsLayer { translationY = floatY },
            contentAlignment = Alignment.Center,
        ) {
            HoneyBadgerFace(mood = mood)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ScrollBuddy · Honey Badger",
                style = MaterialTheme.typography.labelLarge,
                color = palette.highlight,
            )
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedContent(
                targetState = message,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                label = "badgerSpeech",
            ) { text ->
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .background(palette.background, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onBackground,
                    )
                }
            }
        }
    }
}

@Composable
private fun HoneyBadgerFace(mood: BuddyMood) {
    Canvas(modifier = Modifier.size(68.dp)) {
        val w = size.width
        val h = size.height

        // Ears
        drawCircle(BuddyBadgerFur, w * 0.1f, Offset(w * 0.22f, h * 0.28f))
        drawCircle(BuddyBadgerFur, w * 0.1f, Offset(w * 0.78f, h * 0.28f))
        drawCircle(BuddyBadgerCream, w * 0.045f, Offset(w * 0.22f, h * 0.28f))
        drawCircle(BuddyBadgerCream, w * 0.045f, Offset(w * 0.78f, h * 0.28f))

        // Head / body blob
        drawRoundRect(
            color = BuddyBadgerFur,
            topLeft = Offset(w * 0.12f, h * 0.22f),
            size = Size(w * 0.76f, h * 0.62f),
            cornerRadius = CornerRadius(28f, 28f),
        )

        // Signature white dorsal stripe
        val stripe = Path().apply {
            moveTo(w * 0.28f, h * 0.26f)
            quadraticTo(w * 0.5f, h * 0.18f, w * 0.72f, h * 0.26f)
            lineTo(w * 0.68f, h * 0.42f)
            quadraticTo(w * 0.5f, h * 0.34f, w * 0.32f, h * 0.42f)
            close()
        }
        drawPath(stripe, BuddyBadgerStripe)

        // Snout
        drawRoundRect(
            color = BuddyBadgerCream,
            topLeft = Offset(w * 0.34f, h * 0.52f),
            size = Size(w * 0.32f, h * 0.24f),
            cornerRadius = CornerRadius(16f, 16f),
        )
        drawCircle(BuddyBadgerFur, w * 0.03f, Offset(w * 0.5f, h * 0.64f))

        // Eyes / expression
        when (mood) {
            BuddyMood.Cheer -> {
                drawLine(BuddyBadgerFur, Offset(w * 0.34f, h * 0.44f), Offset(w * 0.42f, h * 0.48f), 4f)
                drawLine(BuddyBadgerFur, Offset(w * 0.42f, h * 0.48f), Offset(w * 0.34f, h * 0.52f), 4f)
                drawLine(BuddyBadgerFur, Offset(w * 0.58f, h * 0.48f), Offset(w * 0.66f, h * 0.44f), 4f)
                drawLine(BuddyBadgerFur, Offset(w * 0.58f, h * 0.48f), Offset(w * 0.66f, h * 0.52f), 4f)
            }
            BuddyMood.Rest -> {
                drawLine(BuddyBadgerFur, Offset(w * 0.32f, h * 0.48f), Offset(w * 0.42f, h * 0.48f), 4f)
                drawLine(BuddyBadgerFur, Offset(w * 0.58f, h * 0.48f), Offset(w * 0.68f, h * 0.48f), 4f)
            }
            BuddyMood.Working -> {
                drawCircle(BuddyBadgerFur, w * 0.035f, Offset(w * 0.37f, h * 0.47f))
                drawCircle(BuddyBadgerFur, w * 0.035f, Offset(w * 0.63f, h * 0.47f))
                drawLine(BuddyBadgerFur, Offset(w * 0.3f, h * 0.4f), Offset(w * 0.42f, h * 0.42f), 3.5f)
                drawLine(BuddyBadgerFur, Offset(w * 0.58f, h * 0.42f), Offset(w * 0.7f, h * 0.4f), 3.5f)
            }
            else -> {
                drawCircle(BuddyBadgerFur, w * 0.035f, Offset(w * 0.37f, h * 0.47f))
                drawCircle(BuddyBadgerFur, w * 0.035f, Offset(w * 0.63f, h * 0.47f))
            }
        }
    }
}
