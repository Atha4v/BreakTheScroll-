package com.breakthescroll.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breakthescroll.app.ui.components.buddy.HoneyBadgerAvatar
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.InterestOption
import com.breakthescroll.app.ui.model.InterestUiOptions
import com.breakthescroll.app.ui.theme.LocalBtsPalette

private const val StepWelcome = 0
private const val StepInterests = 1
private const val StepReady = 2
private const val MinInterests = 2

private val EditorialHeadline = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Medium,
    fontSize = 34.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.6).sp,
)

@Composable
fun OnboardingScreen(
    initialInterests: Set<String>,
    isEditing: Boolean,
    onComplete: (Set<String>) -> Unit,
    onSkipOrDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    var selected by remember(initialInterests) { mutableStateOf(initialInterests) }
    var step by remember(isEditing) { mutableIntStateOf(if (isEditing) StepInterests else StepWelcome) }

    BackHandler(enabled = !isEditing && step > StepWelcome) { step -= 1 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        OnboardingTopBar(
            step = step,
            showProgress = !isEditing,
            onBack = when {
                isEditing -> onSkipOrDismiss
                step > StepWelcome -> ({ step -= 1 })
                else -> null
            },
            backIsClose = isEditing,
        )

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                val forward = targetState > initialState
                (slideInHorizontally(tween(320)) { if (forward) it / 6 else -it / 6 } + fadeIn(tween(260))) togetherWith
                    (slideOutHorizontally(tween(260)) { if (forward) -it / 6 else it / 6 } + fadeOut(tween(180)))
            },
            modifier = Modifier.weight(1f),
            label = "onboardingStep",
        ) { current ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            ) {
                when (current) {
                    StepWelcome -> WelcomeStep()
                    StepInterests -> InterestsStep(
                        isEditing = isEditing,
                        selected = selected,
                        onToggle = { tag ->
                            selected = if (tag in selected) selected - tag else selected + tag
                        },
                    )
                    else -> ReadyStep(selected = selected)
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        val remaining = (MinInterests - selected.size).coerceAtLeast(0)
        val ctaEnabled = step != StepInterests || remaining == 0
        BottomActionBar(
            label = when {
                isEditing -> "Save changes"
                step == StepWelcome -> "Get started"
                step == StepInterests -> "Continue"
                else -> "Enter BreakTheScroll"
            },
            caption = when {
                step != StepInterests -> null
                remaining > 0 -> "Select $remaining more to continue"
                else -> "${selected.size} selected"
            },
            enabled = ctaEnabled,
            onClick = {
                when {
                    isEditing -> onComplete(selected)
                    step < StepReady -> step += 1
                    else -> onComplete(selected)
                }
            },
        )
    }
}

@Composable
private fun OnboardingTopBar(
    step: Int,
    showProgress: Boolean,
    onBack: (() -> Unit)?,
    backIsClose: Boolean,
) {
    val palette = LocalBtsPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = if (backIsClose) Icons.Outlined.Close else Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = if (backIsClose) "Close" else "Back",
                        tint = palette.onBackground,
                    )
                }
            }
        }
        if (showProgress) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(3) { index ->
                    val fill by animateFloatAsState(
                        targetValue = if (index <= step) 1f else 0f,
                        animationSpec = tween(360),
                        label = "segment$index",
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(palette.border),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fill)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(palette.onBackground),
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Your interests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = palette.onBackground,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.width(48.dp))
    }
}

@Composable
private fun WelcomeStep() {
    val palette = LocalBtsPalette.current
    Spacer(Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radii = listOf(0.98f, 0.74f, 0.50f)
            radii.forEach { r ->
                drawCircle(
                    color = palette.border,
                    radius = size.minDimension / 2f * r,
                    center = center,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
            val arcInset = size.minDimension * 0.13f
            drawArc(
                color = palette.primary,
                startAngle = -90f,
                sweepAngle = 250f,
                useCenter = false,
                topLeft = Offset(arcInset, arcInset),
                size = androidx.compose.ui.geometry.Size(
                    size.width - arcInset * 2,
                    size.height - arcInset * 2,
                ),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
            drawCircle(
                color = palette.highlight,
                radius = 5.dp.toPx(),
                center = Offset(center.x + size.minDimension / 2f * 0.98f * 0.707f, center.y - size.minDimension / 2f * 0.98f * 0.707f),
            )
            drawCircle(
                color = palette.onBackground,
                radius = 3.dp.toPx(),
                center = Offset(center.x - size.minDimension / 2f * 0.50f, center.y + size.minDimension * 0.08f),
            )
        }
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(CircleShape)
                .background(palette.surface)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            HoneyBadgerAvatar(mood = BuddyMood.Idle, modifier = Modifier.size(72.dp))
        }
    }

    Spacer(Modifier.height(20.dp))
    Overline("BreakTheScroll")
    Spacer(Modifier.height(10.dp))
    Text(
        text = "Take back your attention.",
        style = EditorialHeadline,
        color = palette.onBackground,
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = "Trade the next mindless scroll for a short, real-world reset.",
        style = MaterialTheme.typography.bodyLarge,
        color = palette.onSurfaceMuted,
    )
    Spacer(Modifier.height(28.dp))

    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        ValueRow(Icons.Outlined.AutoAwesome, "Breaks built around you", "Micro-tasks from the things you love.")
        ValueRow(Icons.Outlined.Bolt, "One-tap scroll rescue", "Hit START the moment the urge shows up.")
        ValueRow(Icons.Outlined.LocalFireDepartment, "Streaks that mean something", "Every finished timer counts.")
    }
}

@Composable
private fun ValueRow(icon: ImageVector, title: String, subtitle: String) {
    val palette = LocalBtsPalette.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(palette.surfaceElevated),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = palette.onBackground, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = palette.onBackground,
            )
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = palette.onSurfaceMuted)
        }
    }
}

@Composable
private fun InterestsStep(
    isEditing: Boolean,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    val palette = LocalBtsPalette.current
    Spacer(Modifier.height(12.dp))
    if (!isEditing) {
        Overline("Personalize")
        Spacer(Modifier.height(10.dp))
    }
    Text(
        text = "What would you rather be doing?",
        style = EditorialHeadline.copy(fontSize = 30.sp, lineHeight = 36.sp),
        color = palette.onBackground,
    )
    Spacer(Modifier.height(10.dp))
    Text(
        text = "Pick at least two. Your For You deck is built only from these.",
        style = MaterialTheme.typography.bodyLarge,
        color = palette.onSurfaceMuted,
    )
    Spacer(Modifier.height(24.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InterestUiOptions.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { option ->
                    InterestTile(
                        option = option,
                        selected = option.tag in selected,
                        onClick = { onToggle(option.tag) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InterestTile(
    option: InterestOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val container by animateColorAsState(
        if (selected) palette.primaryContainer else palette.surface,
        label = "tileBg",
    )
    val borderColor by animateColorAsState(
        if (selected) palette.primary else palette.border,
        label = "tileBorder",
    )
    val iconBg by animateColorAsState(
        if (selected) palette.primary else palette.surfaceElevated,
        label = "tileIconBg",
    )
    val scale by animateFloatAsState(if (selected) 0.98f else 1f, label = "tileScale")
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .scale(scale)
            .height(132.dp)
            .clip(shape)
            .background(container)
            .border(if (selected) 1.5.dp else 1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = if (selected) palette.onPrimary else palette.onBackground,
                    modifier = Modifier.size(22.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (selected) palette.primary else palette.surface)
                    .border(1.dp, if (selected) palette.primary else palette.border, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = palette.onPrimary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
        Column {
            Text(
                text = option.tag,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = palette.onBackground,
            )
            Text(
                text = option.subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = palette.onSurfaceMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReadyStep(selected: Set<String>) {
    val palette = LocalBtsPalette.current
    Spacer(Modifier.height(12.dp))
    Overline("All set")
    Spacer(Modifier.height(10.dp))
    Text(
        text = "Your deck is ready.",
        style = EditorialHeadline,
        color = palette.onBackground,
    )
    Spacer(Modifier.height(20.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(22.dp))
            .padding(18.dp),
    ) {
        Text(
            "For You is built from",
            style = MaterialTheme.typography.labelLarge,
            color = palette.onSurfaceMuted,
        )
        Spacer(Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InterestUiOptions.filter { it.tag in selected }.forEach { option ->
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(palette.surfaceElevated)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(option.icon, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
                    Text(option.tag, style = MaterialTheme.typography.labelLarge, color = palette.onBackground)
                }
            }
        }
    }

    Spacer(Modifier.height(24.dp))
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        HowItWorksRow("01", "Swipe your deck", "Engage starts a real timer — no instant ticks.")
        HowItWorksRow("02", "Tap START when the urge hits", "A 45–60 second rescue, no menus.")
        HowItWorksRow("03", "Watch it compound", "Streaks, hours saved, and history in Progress.")
    }
}

@Composable
private fun HowItWorksRow(number: String, title: String, subtitle: String) {
    val palette = LocalBtsPalette.current
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = number,
            style = EditorialHeadline.copy(fontSize = 20.sp, lineHeight = 24.sp),
            color = palette.primary,
        )
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = palette.onBackground,
            )
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = palette.onSurfaceMuted)
        }
    }
}

@Composable
private fun Overline(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.8.sp,
        ),
        color = LocalBtsPalette.current.primary,
    )
}

@Composable
private fun BottomActionBar(
    label: String,
    caption: String?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalBtsPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.background)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelLarge,
                color = palette.onSurfaceMuted,
            )
            Spacer(Modifier.height(10.dp))
        }
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.onBackground,
                contentColor = palette.background,
                disabledContainerColor = palette.surfaceElevated,
                disabledContentColor = palette.onSurfaceMuted,
            ),
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}
