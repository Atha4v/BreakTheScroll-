package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.breakthescroll.app.R
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitCompletionEntity
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.components.streak.StreakFireBadge
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import com.breakthescroll.app.ui.viewmodel.ProgressUiState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(
    state: ProgressUiState,
    buddyMood: BuddyMood,
    buddyMessage: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val hours = state.stats.screenTimeSavedMinutes / 60f
    val formatter = remember {
        DateTimeFormatter.ofPattern("EEE, MMM d · HH:mm").withZone(ZoneId.systemDefault())
    }
    val timeOnly = remember {
        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.progress_title),
            style = MaterialTheme.typography.displaySmall,
            color = palette.onBackground,
        )
        Spacer(modifier = Modifier.height(12.dp))

        ScrollBuddyBanner(mood = buddyMood, customMessage = buddyMessage)
        Spacer(modifier = Modifier.height(16.dp))

        StreakFireBadge(streak = state.stats.currentStreak, longest = state.stats.longestStreak)

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricTile("Hours saved", String.format("%.1f", hours), palette.primary, Modifier.weight(1f))
            MetricTile("Tasks done", "${state.stats.tasksCompleted}", palette.highlight, Modifier.weight(1f))
        }

        if (state.stats.quickRescues > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.primaryContainer.copy(alpha = 0.55f))
                    .border(1.dp, palette.primary.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("⚡", style = MaterialTheme.typography.headlineSmall)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.quick_rescue_badge),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.onBackground,
                    )
                    Text(
                        text = "${state.stats.quickRescues} instant breaker${if (state.stats.quickRescues == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceMuted,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        PanelCard("Habits completed today") {
            if (state.habitsToday.isEmpty()) {
                Text(
                    "None yet today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceMuted,
                )
            } else {
                state.habitsToday.forEach { habit ->
                    HabitHistoryRow(
                        item = habit,
                        timeLabel = timeOnly.format(Instant.ofEpochMilli(habit.completedAt)),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        PanelCard("Habits this week") {
            if (state.habitsWeek.isEmpty()) {
                Text(
                    "Empty this week.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceMuted,
                )
            } else {
                Text(
                    "${state.habitsWeek.size} completions · +${state.habitsWeek.size * 2} min focus",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.highlight,
                )
                Spacer(modifier = Modifier.height(10.dp))
                state.habitsWeek.take(12).forEach { habit ->
                    HabitHistoryRow(
                        item = habit,
                        timeLabel = formatter.format(Instant.ofEpochMilli(habit.completedAt)),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        PanelCard("This week") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                state.weeklyBars.forEachIndexed { index, value ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((110 * value).dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.primary.copy(alpha = 0.25f + value * 0.65f)),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(days[index], style = MaterialTheme.typography.labelMedium, color = palette.onSurfaceMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PanelCard("Streak heatmap") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                state.heatmap.forEach { intensity ->
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(palette.primary.copy(alpha = intensity)),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PanelCard("Activity history") {
            if (state.recent.isEmpty()) {
                Text(
                    "No activity wins yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceMuted,
                )
            } else {
                state.recent.forEach { item ->
                    HistoryRow(
                        item = item,
                        timeLabel = formatter.format(Instant.ofEpochMilli(item.completedAt)),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
private fun HabitHistoryRow(
    item: HabitCompletionEntity,
    timeLabel: String,
) {
    val palette = LocalBtsPalette.current
    val accent = Color(item.accentArgb.toLong() and 0xFFFFFFFFL)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.background)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Spa, contentDescription = null, tint = accent)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(item.habitTitle, style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
            Text("Habit · +2 min focus", style = MaterialTheme.typography.labelMedium, color = palette.highlight)
            Text(timeLabel, style = MaterialTheme.typography.bodySmall, color = palette.onSurfaceMuted)
        }
    }
}

@Composable
private fun HistoryRow(
    item: ActivityCompletionEntity,
    timeLabel: String,
) {
    val palette = LocalBtsPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.background)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (item.imageUrl.isNotBlank()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(palette.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = palette.primary)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
            Text(
                "${item.category} · ${item.durationMinutes} min",
                style = MaterialTheme.typography.labelMedium,
                color = palette.highlight,
            )
            Text(timeLabel, style = MaterialTheme.typography.bodySmall, color = palette.onSurfaceMuted)
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = accent)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = palette.onSurfaceMuted)
    }
}

@Composable
private fun PanelCard(
    title: String,
    content: @Composable () -> Unit,
) {
    val palette = LocalBtsPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(22.dp))
            .padding(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
        Spacer(modifier = Modifier.height(14.dp))
        content()
    }
}
