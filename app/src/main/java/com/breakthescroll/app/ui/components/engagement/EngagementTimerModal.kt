package com.breakthescroll.app.ui.components.engagement

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.breakthescroll.app.ui.buddy.HoneyBadgerManager
import com.breakthescroll.app.ui.components.buddy.HoneyBadgerAvatar
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.ActivityGuides
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import kotlinx.coroutines.delay

private enum class ActiveBreakPhase { Counting, Success }

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
    val steps = remember(challenge.id) {
        ActivityGuides.stepsFor(challenge.id, challenge.category)
    }
    val buddyLine = remember(challenge.id) { HoneyBadgerManager.quoteForTimer() }

    var remainingMs by remember(challenge.id) { mutableLongStateOf(totalMs) }
    var phase by remember(challenge.id) { mutableStateOf(ActiveBreakPhase.Counting) }
    var lastWholeSecond by remember(challenge.id) { mutableStateOf(totalSeconds) }

    val progress = 1f - (remainingMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
    val remainingSeconds = ((remainingMs + 999) / 1000).toInt().coerceAtLeast(0)
    val clock = "%d:%02d".format(remainingSeconds / 60, remainingSeconds % 60)

    val breath = rememberInfiniteTransition(label = "breath")
    val pulse by breath.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val successScale = remember { Animatable(0.7f) }

    BackHandler(enabled = phase == ActiveBreakPhase.Counting, onBack = onDismiss)

    LaunchedEffect(challenge.id) {
        remainingMs = totalMs
        phase = ActiveBreakPhase.Counting
        lastWholeSecond = totalSeconds
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        val startedAt = SystemClock.elapsedRealtime()
        while (true) {
            val left = (totalMs - (SystemClock.elapsedRealtime() - startedAt)).coerceAtLeast(0L)
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
        successScale.snapTo(0.7f)
        successScale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 340f))
        delay(1_200)
        onCompleted()
    }

    Dialog(
        onDismissRequest = { if (phase == ActiveBreakPhase.Counting) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = phase == ActiveBreakPhase.Counting,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Immersive calm backdrop — soft radial wash + breathing orbs
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            palette.background,
                            if (palette.isDark) Color(0xFF132033) else Color(0xFFF1F5F9),
                            palette.primaryContainer.copy(alpha = if (palette.isDark) 0.55f else 0.45f),
                            palette.background,
                        ),
                    ),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.primary.copy(alpha = if (palette.isDark) 0.22f else 0.18f),
                            Color.Transparent,
                        ),
                        center = Offset(w * 0.5f, h * 0.38f),
                        radius = w * 0.55f,
                    ),
                    center = Offset(w * 0.5f, h * 0.38f),
                    radius = w * 0.55f,
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.highlight.copy(alpha = if (palette.isDark) 0.14f else 0.12f),
                            Color.Transparent,
                        ),
                        center = Offset(w * 0.18f, h * 0.78f),
                        radius = w * 0.42f,
                    ),
                    center = Offset(w * 0.18f, h * 0.78f),
                    radius = w * 0.42f,
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.primary.copy(alpha = 0.10f),
                            Color.Transparent,
                        ),
                        center = Offset(w * 0.88f, h * 0.18f),
                        radius = w * 0.35f,
                    ),
                    center = Offset(w * 0.88f, h * 0.18f),
                    radius = w * 0.35f,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (challenge.id.startsWith("rescue-")) "Instant Scroll Breaker" else challenge.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.onBackground,
                    textAlign = TextAlign.Center,
                )
                if (challenge.id.startsWith("rescue-")) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = palette.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                Text(
                    text = buddyLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, start = 12.dp, end = 12.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                AnimatedContent(
                    targetState = phase,
                    transitionSpec = {
                        (fadeIn(tween(200)) + scaleIn(initialScale = 0.95f)) togetherWith
                            fadeOut(tween(120))
                    },
                    label = "timerPhase",
                ) { current ->
                    when (current) {
                        ActiveBreakPhase.Counting -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(280.dp),
                                ) {
                                    Canvas(
                                        modifier = Modifier
                                            .size(280.dp)
                                            .graphicsLayer {
                                                scaleX = pulse
                                                scaleY = pulse
                                            },
                                    ) {
                                        val stroke = 12.dp.toPx()
                                        val diameter = size.minDimension - stroke
                                        val topLeft = Offset(
                                            (size.width - diameter) / 2f,
                                            (size.height - diameter) / 2f,
                                        )
                                        drawArc(
                                            color = palette.border.copy(alpha = 0.55f),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = Size(diameter, diameter),
                                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                                        )
                                        drawArc(
                                            color = palette.primary,
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
                                            modifier = Modifier.size(56.dp),
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = clock,
                                            style = MaterialTheme.typography.displayLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 56.sp,
                                                letterSpacing = (-1.5).sp,
                                            ),
                                            color = palette.onBackground,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(28.dp))
                                steps.take(2).forEachIndexed { index, step ->
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (index == 0) palette.onBackground else palette.onSurfaceMuted,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 4.dp),
                                    )
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
                                HoneyBadgerAvatar(mood = BuddyMood.Cheer, modifier = Modifier.size(72.dp))
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (challenge.id.startsWith("rescue-")) "Rescue complete" else "Done 🔥",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = palette.onBackground,
                                )
                                Text(
                                    text = when {
                                        challenge.id.startsWith("rescue-") -> "+1 Quick Rescue"
                                        currentStreak > 0 -> "Streak warming · ${currentStreak + 1}"
                                        else -> "Saved to history"
                                    },
                                    color = palette.highlight,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (phase == ActiveBreakPhase.Counting) {
                    TextButton(onClick = onDismiss) {
                        Text("Give up", color = palette.onSurfaceMuted)
                    }
                } else {
                    Text(
                        "Saving…",
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
