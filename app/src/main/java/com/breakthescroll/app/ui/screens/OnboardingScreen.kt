package com.breakthescroll.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.InterestOption
import com.breakthescroll.app.ui.model.InterestUiOptions
import com.breakthescroll.app.ui.theme.LocalBtsPalette

@OptIn(ExperimentalLayoutApi::class)
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (isEditing) "Tune your mix" else "What pulls you offline?",
            style = MaterialTheme.typography.displaySmall,
            color = palette.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick topics. We build a 50/50 deck — half yours, half surprise wellness.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.onSurfaceMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ScrollBuddyBanner(
            mood = BuddyMood.Idle,
            customMessage = "Two topics minimum. The algorithm isn't your friend — I am.",
        )
        Spacer(modifier = Modifier.height(22.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            InterestUiOptions.forEach { option ->
                InterestPill(
                    option = option,
                    selected = option.tag in selected,
                    onClick = {
                        selected = if (option.tag in selected) {
                            selected - option.tag
                        } else {
                            selected + option.tag
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { onComplete(selected) },
            enabled = selected.size >= 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.primary,
                contentColor = palette.onPrimary,
                disabledContainerColor = palette.primaryContainer,
                disabledContentColor = palette.onSurfaceMuted,
            ),
        ) {
            Text(if (isEditing) "Save preferences" else "Build my hybrid deck")
        }

        if (onSkipOrDismiss != null) {
            TextButton(onClick = onSkipOrDismiss) {
                Text(
                    if (isEditing) "Cancel" else "Skip for now",
                    color = palette.onSurfaceMuted,
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun InterestPill(
    option: InterestOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalBtsPalette.current
    val bg by animateColorAsState(
        if (selected) palette.primary else palette.surface,
        label = "pillBg",
    )
    val border by animateColorAsState(
        if (selected) palette.primary else palette.border,
        label = "pillBorder",
    )
    val content by animateColorAsState(
        if (selected) palette.onPrimary else palette.onBackground,
        label = "pillContent",
    )
    val scale by animateFloatAsState(if (selected) 1.03f else 1f, label = "pillScale")

    Row(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            tint = if (selected) palette.onPrimary else palette.primary,
            modifier = Modifier.size(22.dp),
        )
        Column {
            Text(
                text = option.tag,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = content,
            )
            Text(
                text = option.subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) palette.onPrimary.copy(alpha = 0.8f) else palette.onSurfaceMuted,
            )
        }
    }
}
