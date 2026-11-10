package com.breakthescroll.app.ui.components.engagement

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.breakthescroll.app.ui.components.buddy.HoneyBadgerAvatar
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import kotlinx.coroutines.delay

private enum class ActiveBreakPhase {
    Counting,
    Success,
}

/**
 * Headspace / Endel-inspired Active Break.
 * Countdown length comes from [ActivityChallenge.durationSeconds] in Room — never a hardcoded 25s.
 */
@Composable
fun EngagementTimerModal(
    challenge: ActivityChallenge,
    currentStreak: Int,
    onCompleted: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = LocalBtsPalette.current
    val haptics = LocalHapticFeedback.current
    val totalSeconds = challenge.durationSeconds.coerceAtLeast(15)
    val totalMs = totalSeconds * 1000L

    var remainingMs by remember(challenge.id) { mutableLongStateOf(totalMs) }
    var phase by remember(challenge.id) { mutableStateOf(ActiveBreakPhase.Counting) }
    var lastWholeSecond by remember(challenge.id) { mutableStateOf(totalSeconds) }

    val progress = 1f - (remainingMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
    val remainingSeconds = ((remainingMs + 999) / 1000).toInt().coerceAtLeast(0)

    val breath = rememberInfiniteTransition(label = "breath")
    val pulse by breath.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val glow by breath.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )
    val successScale = remember { Animatable(0.65f) }

    BackHandler(enabled = phase == ActiveBreakPhase.Counting, onBack = onDismiss)

    LaunchedEffect(challenge.id) {
        remainingMs = totalMs
        phase = ActiveBreakPhase.Counting
        lastWholeSecond = totalSeconds
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        val startedAt = SystemClock.elapsedRealtime()
        while (true) {
            val elapsed = SystemClock.elapsedRealtime() - startedAt
            val left = (totalMs - elapsed).coerceAtLeast(0L)
            remainingMs = left
            val whole = ((left + 999) / 1000).toInt()
            if (whole != lastWholeSecond && whole > 0) {
                lastWholeSecond = whole
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            if (left <= 0L) break
            delay(32)
        }
        phase = ActiveBreakPhase.Success
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        successScale.snapTo(0.65f)
        successScale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 360f))
        delay(1_400)
        onCompleted()
    }

    val clock = formatClock(remainingSeconds)

    Dialog(
        onDismissRequest = {
            if (phase == ActiveBreakPhase.Counting) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = phase == ActiveBreakPhase.Counting,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070B12)),
        ) {
            if (challenge.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = challenge.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.28f },
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                palette.primary.copy(alpha = 0.22f),
                                Color.Transparent,
                                Color(0xFF05080F),
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "ACTIVE BREAK",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = palette.highlight,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "${challenge.category} · ${formatDurationLabel(totalSeconds)} session",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.65f),
                )

                Spacer(modifier = Modifier.weight(1f))

                AnimatedContent(
                    targetState = phase,
                    transitionSpec = {
                        (fadeIn(tween(220)) + scaleIn(initialScale = 0.94f)) togetherWith
                            fadeOut(tween(140))
                    },
                    label = "breakPhase",
                ) { current ->
                    when (current) {
                        ActiveBreakPhase.Counting -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(300.dp),
                                ) {
                                    Canvas(
                                        modifier = Modifier
                                            .size(300.dp)
                                            .graphicsLayer {
                                                scaleX = pulse
                                                scaleY = pulse
                                                alpha = 0.5f + glow * 0.45f
                                            },
                                    ) {
                                        val stroke = 16.dp.toPx()
                                        val diameter = size.minDimension - stroke
                                        val topLeft = Offset(
                                            (size.width - diameter) / 2f,
                                            (size.height - diameter) / 2f,
                                        )
                                        drawArc(
                                            color = Color.White.copy(alpha = 0.10f),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = Size(diameter, diameter),
                                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                                        )
                                        drawArc(
                                            brush = Brush.sweepGradient(
                                                listOf(
                                                    palette.primary.copy(alpha = 0.35f),
                                                    palette.primary,
                                                    palette.highlight,
                                                    palette.primary,
                                                ),
                                            ),
                                            startAngle = -90f,
                                            sweepAngle = 360f * progress,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = Size(diameter, diameter),
                                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        HoneyBadgerAvatar(
                                            mood = BuddyMood.Working,
                                            modifier = Modifier
                                                .size(72.dp)
                                                .graphicsLayer {
                                                    scaleX = pulse
                                                    scaleY = pulse
                                                },
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = clock,
                                            style = MaterialTheme.typography.displayLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 52.sp,
                                                letterSpacing = (-1).sp,
                                            ),
                                            color = Color.White,
                                        )
                                        Text(
                                            text = "breathe with the ring",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White.copy(alpha = 0.62f),
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    HintChip(Icons.Outlined.Vibration, "Haptics")
                                    HintChip(Icons.Outlined.GraphicEq, "Stay present")
                                }
                            }
                        }
                        ActiveBreakPhase.Success -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = successScale.value
                                    scaleY = successScale.value
                                },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(palette.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    HoneyBadgerAvatar(
                                        mood = BuddyMood.Cheer,
                                        modifier = Modifier.size(76.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Break complete 🔥",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "Saving to history · streak warming up",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = palette.highlight,
                                )
                                if (currentStreak > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "🔥 ${currentStreak + 1}-day energy incoming",
                                        color = Color.White.copy(alpha = 0.85f),
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (phase == ActiveBreakPhase.Counting) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color.White.copy(alpha = 0.10f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f),
                        ),
                    ) {
                        Text("Timer locked · $clock remaining")
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Give up — return to feed", color = Color.White.copy(alpha = 0.7f))
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(palette.primary.copy(alpha = 0.18f))
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "Recording win…",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.primary,
                        )
                    }
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}

@Composable
private fun HintChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.75f), modifier = Modifier.size(16.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.75f))
    }
}

private fun formatClock(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%d:%02d".format(m, s)
}

private fun formatDurationLabel(totalSeconds: Int): String {
    return if (totalSeconds < 60) {
        "${totalSeconds}s"
    } else {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        if (s == 0) "${m}m" else "${m}m ${s}s"
    }
}
