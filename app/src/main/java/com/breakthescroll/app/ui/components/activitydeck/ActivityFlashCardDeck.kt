package com.breakthescroll.app.ui.components.activitydeck

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import com.breakthescroll.app.ui.theme.OnSurfaceSecondary
import kotlinx.coroutines.launch
import kotlin.math.abs


enum class DeckSwipeAction {
    Accept,
    Pass,
}

private val SwipeExitSpring = spring<Offset>(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

private val SwipeReturnSpring = spring<Offset>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium,
)

@Composable
fun ActivityFlashCardDeck(
    challenges: List<ActivityChallenge>,
    modifier: Modifier = Modifier,
    onAccepted: (ActivityChallenge) -> Unit = {},
    onPassed: (ActivityChallenge) -> Unit = {},
    onDeckExhausted: () -> Unit = {},
) {
    var remaining by remember(challenges) { mutableStateOf(challenges) }
    var activeIndex by remember(challenges) { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isAnimatingOut by remember { mutableStateOf(false) }
    var dragVelocityX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(challenges) {
        remaining = challenges
        activeIndex = 0
        offset.snapTo(Offset.Zero)
        isAnimatingOut = false
    }

    val topCard = remaining.firstOrNull()
    val stackedCards = remaining.drop(1).take(2)
    val totalCount = challenges.size
    val completedCount = (activeIndex).coerceAtMost(totalCount)

    fun settleOrSwipe(
        velocity: Offset,
        cardWidthPx: Float,
        challenge: ActivityChallenge,
    ) {
        scope.launch {
            val horizontal = offset.value.x
            val threshold = cardWidthPx * 0.28f
            val fling = abs(velocity.x) > 1200f
            val shouldAccept = horizontal > threshold || (fling && velocity.x > 0f)
            val shouldPass = horizontal < -threshold || (fling && velocity.x < 0f)

            when {
                shouldAccept || shouldPass -> {
                    isAnimatingOut = true
                    val direction = if (shouldAccept) 1f else -1f
                    val target = Offset(
                        x = direction * cardWidthPx * 1.45f,
                        y = offset.value.y + (direction * 40f),
                    )
                    offset.animateTo(target, SwipeExitSpring)
                    if (shouldAccept) {
                        onAccepted(challenge)
                    } else {
                        onPassed(challenge)
                    }
                    remaining = remaining.drop(1)
                    activeIndex += 1
                    offset.snapTo(Offset.Zero)
                    isAnimatingOut = false
                    if (remaining.isEmpty()) {
                        onDeckExhausted()
                    }
                }
                else -> offset.animateTo(Offset.Zero, SwipeReturnSpring)
            }
        }
    }

    fun programmaticSwipe(action: DeckSwipeAction) {
        val challenge = remaining.firstOrNull() ?: return
        if (isAnimatingOut) return
        scope.launch {
            isAnimatingOut = true
            val cardWidthPx = with(density) { 320.dp.toPx() }
            val direction = if (action == DeckSwipeAction.Accept) 1f else -1f
            offset.animateTo(
                targetValue = Offset(x = direction * cardWidthPx * 1.45f, y = -80f),
                animationSpec = tween(durationMillis = 280),
            )
            if (action == DeckSwipeAction.Accept) {
                onAccepted(challenge)
            } else {
                onPassed(challenge)
            }
            remaining = remaining.drop(1)
            activeIndex += 1
            offset.snapTo(Offset.Zero)
            isAnimatingOut = false
            if (remaining.isEmpty()) {
                onDeckExhausted()
            }
        }
    }

    val palette = LocalBtsPalette.current
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (totalCount == 0) {
                    "0 left"
                } else {
                    "${(totalCount - completedCount).coerceAtLeast(0)} left · $completedCount done"
                },
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceSecondary,
            )
            if (remaining.isEmpty() && challenges.isNotEmpty()) {
                OutlinedIconButton(
                    onClick = {
                        remaining = challenges
                        activeIndex = 0
                    },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Reset deck",
                        tint = palette.primary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (topCard == null) {
                EmptyDeckState()
            } else {
                stackedCards
                    .asReversed()
                    .forEachIndexed { reverseIndex, challenge ->
                        val depth = stackedCards.size - reverseIndex
                        val scale = 1f - (depth * 0.05f)
                        val yOffset = depth * 14f
                        key(challenge.id) {
                            ActivityCard(
                                challenge = challenge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        translationY = yOffset
                                        alpha = 1f - (depth * 0.12f)
                                    },
                            )
                        }
                    }

                key(topCard.id) {
                    val swipeProgress = (offset.value.x / with(density) { 220.dp.toPx() })
                        .coerceIn(-1f, 1f)
                    val rotation = (offset.value.x / 60f).coerceIn(-14f, 14f)

                    ActivityCard(
                        challenge = topCard,
                        swipeProgress = swipeProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .graphicsLayer {
                                translationX = offset.value.x
                                translationY = offset.value.y
                                rotationZ = rotation
                            }
                            .pointerInput(topCard.id, isAnimatingOut) {
                                if (isAnimatingOut) return@pointerInput
                                detectDragGestures(
                                    onDragStart = { dragVelocityX = 0f },
                                    onDragEnd = {
                                        settleOrSwipe(
                                            velocity = Offset(dragVelocityX, 0f),
                                            cardWidthPx = size.width.toFloat(),
                                            challenge = topCard,
                                        )
                                    },
                                    onDragCancel = {
                                        scope.launch {
                                            offset.animateTo(Offset.Zero, SwipeReturnSpring)
                                        }
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        // Frame-scaled horizontal velocity estimate for fling settle.
                                        dragVelocityX = (dragAmount.x * 60f).coerceIn(-6000f, 6000f)
                                        scope.launch {
                                            offset.snapTo(offset.value + dragAmount)
                                        }
                                    },
                                )
                            },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        DeckActionRow(
            enabled = topCard != null && !isAnimatingOut,
            onPass = { programmaticSwipe(DeckSwipeAction.Pass) },
            onAccept = { programmaticSwipe(DeckSwipeAction.Accept) },
        )
    }
}

@Composable
private fun DeckActionRow(
    enabled: Boolean,
    onPass: () -> Unit,
    onAccept: () -> Unit,
) {
    val palette = LocalBtsPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedIconButton(
                onClick = onPass,
                enabled = enabled,
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = palette.error,
                    disabledContentColor = palette.error.copy(alpha = 0.35f),
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.action_pass),
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.action_pass),
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) palette.error else palette.error.copy(alpha = 0.35f),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FilledIconButton(
                onClick = onAccept,
                enabled = enabled,
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = palette.primary,
                    contentColor = palette.onPrimary,
                    disabledContainerColor = palette.primaryContainer,
                    disabledContentColor = palette.primary.copy(alpha = 0.45f),
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = stringResource(R.string.action_accept),
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.action_accept),
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) palette.primary else palette.primary.copy(alpha = 0.35f),
            )
        }
    }
}

@Composable
private fun EmptyDeckState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.deck_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.deck_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

