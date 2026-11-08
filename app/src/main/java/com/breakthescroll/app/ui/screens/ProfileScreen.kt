package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.data.local.entity.UserStatsEntity
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.LocalBtsPalette

@Composable
fun ProfileScreen(
    displayName: String,
    stats: UserStatsEntity,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.profile_title), stringResource(R.string.groups_title))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(displayName, style = MaterialTheme.typography.displaySmall, color = palette.onBackground)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Streak ${stats.currentStreak} · ${stats.tasksCompleted} tasks · Honey Badger ready",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.onSurfaceMuted,
        )

        Spacer(modifier = Modifier.height(14.dp))

        SecondaryTabRow(
            selectedTabIndex = tabIndex,
            containerColor = palette.surface,
            contentColor = palette.onBackground,
            modifier = Modifier.clip(RoundedCornerShape(14.dp)),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        Text(
                            title,
                            color = if (tabIndex == index) palette.primary else palette.onSurfaceMuted,
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tabIndex == 0) {
            ScrollBuddyBanner(
                mood = BuddyMood.Rest,
                customMessage = "Dark/Light jo bhi — persistence same. Never give up.",
            )
            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            if (darkTheme) "Dark mode" else "Light mode",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.onBackground,
                        )
                        Text(
                            if (darkTheme) {
                                "Deep Slate · Serene Mint · Warm Amber"
                            } else {
                                "Pristine White · Resilient Copper · Badger Gold"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.onSurfaceMuted,
                        )
                    }
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onDarkThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.onPrimary,
                            checkedTrackColor = palette.primary,
                            uncheckedThumbColor = palette.primary,
                            uncheckedTrackColor = palette.primaryContainer,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Stats snapshot", style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
                Text("Longest streak: ${stats.longestStreak} days", color = palette.onSurfaceMuted)
                Text("Screen time saved: ${stats.screenTimeSavedMinutes} min", color = palette.onSurfaceMuted)
                Text("Theme preference saved in Room", color = palette.onSurfaceMuted)
            }
        } else {
            Text(
                "Squad challenges sync later — local Room logs power your personal streak today.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.onSurfaceMuted,
            )
            Spacer(modifier = Modifier.height(12.dp))
            listOf(
                "Dawn Movers · join shared morning walks",
                "Focus Pod · no-doomscroll evenings",
                "Hydration Crew · glass-for-glass accountability",
            ).forEach { line ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                ) {
                    Text(line, style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
                    Text(
                        "Community stream coming next — your completions already persist locally.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceMuted,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}
