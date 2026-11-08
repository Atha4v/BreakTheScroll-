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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import com.breakthescroll.app.ui.viewmodel.ProgressUiState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(
    state: ProgressUiState,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val hours = state.stats.screenTimeSavedMinutes / 60f

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
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Live Room analytics — no dummy progress.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.onSurfaceMuted,
        )
        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricTile("Hours saved", String.format("%.1f", hours), palette.primary, Modifier.weight(1f))
            MetricTile("Tasks done", "${state.stats.tasksCompleted}", palette.highlight, Modifier.weight(1f))
            MetricTile("Streak", "${state.stats.currentStreak}d", palette.onBackground, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

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

        PanelCard("Completion log") {
            if (state.recent.isEmpty()) {
                Text(
                    "Accept activities or finish START resets to build history.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceMuted,
                )
            } else {
                val formatter = DateTimeFormatter.ofPattern("MMM d · HH:mm")
                    .withZone(ZoneId.systemDefault())
                state.recent.forEach { item ->
                    Text(
                        text = "• ${item.title} · ${item.durationMinutes} min · ${formatter.format(Instant.ofEpochMilli(item.completedAt))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onBackground,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    accent: androidx.compose.ui.graphics.Color,
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
