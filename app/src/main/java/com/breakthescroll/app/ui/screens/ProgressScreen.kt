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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.model.SampleProgress
import com.breakthescroll.app.ui.theme.BorderObsidian
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.DeepSlate
import com.breakthescroll.app.ui.theme.Obsidian
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.SoftFrostMuted
import com.breakthescroll.app.ui.theme.WarmAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.progress_title),
            style = MaterialTheme.typography.displaySmall,
            color = SoftFrost,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hours reclaimed, streaks kept, and calm momentum.",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftFrostMuted,
        )
        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricTile(
                label = "Hours saved",
                value = "${SampleProgress.hoursSavedThisWeek}",
                accent = SereneMint,
                modifier = Modifier.weight(1f),
            )
            MetricTile(
                label = "Tasks done",
                value = "${SampleProgress.tasksCompleted}",
                accent = WarmAmber,
                modifier = Modifier.weight(1f),
            )
            MetricTile(
                label = "Streak",
                value = "${SampleProgress.currentStreak}d",
                accent = SoftFrost,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        PanelCard(title = "This week") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                SampleProgress.weeklyBars.forEachIndexed { index, value ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((110 * value).dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SereneMint.copy(alpha = 0.35f + value * 0.55f)),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(days[index], style = MaterialTheme.typography.labelMedium, color = SoftFrostMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PanelCard(title = "Streak heatmap") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SampleProgress.heatmap.forEach { intensity ->
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(SereneMint.copy(alpha = 0.15f + intensity * 0.75f)),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Darker cells = deeper offline wins.",
                style = MaterialTheme.typography.labelMedium,
                color = SoftFrostMuted,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PanelCard(title = "Recent completions") {
            listOf(
                "Desk yoga stretch · 6 min",
                "Hydration pause · 2 min",
                "Box breathing reset · 2 min",
                "Five-minute walk · 5 min",
            ).forEachIndexed { index, item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftFrost,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                if (index < 3) {
                    Spacer(modifier = Modifier.height(2.dp))
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
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Obsidian)
            .border(1.dp, BorderObsidian, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = accent)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = SoftFrostMuted)
    }
}

@Composable
private fun PanelCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Obsidian)
            .border(1.dp, BorderObsidian, RoundedCornerShape(22.dp))
            .padding(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = SoftFrost)
        Spacer(modifier = Modifier.height(14.dp))
        content()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ProgressScreenPreview() {
    BreakTheScrollTheme { ProgressScreen() }
}
