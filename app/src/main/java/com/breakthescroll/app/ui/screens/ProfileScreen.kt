package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.SampleProfile
import com.breakthescroll.app.ui.theme.BorderObsidian
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.DeepSlate
import com.breakthescroll.app.ui.theme.Obsidian
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.SoftFrostMuted
import com.breakthescroll.app.ui.theme.WarmAmber

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.profile_title),
        stringResource(R.string.groups_title),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = SampleProfile.displayName,
            style = MaterialTheme.typography.displaySmall,
            color = SoftFrost,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Companion Lv.${SampleProfile.companionLevel} · Calm streak keeper",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftFrostMuted,
        )

        Spacer(modifier = Modifier.height(14.dp))

        SecondaryTabRow(
            selectedTabIndex = tabIndex,
            containerColor = Obsidian,
            contentColor = SoftFrost,
            modifier = Modifier.clip(RoundedCornerShape(14.dp)),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        Text(
                            title,
                            color = if (tabIndex == index) SereneMint else SoftFrostMuted,
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (tabIndex) {
            0 -> ProfileTabContent()
            else -> SquadsTabContent()
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
private fun ProfileTabContent() {
    ScrollBuddyBanner(mood = BuddyMood.Rest, customMessage = "Main ready hoon jab tum offline jao 🦊")
    Spacer(modifier = Modifier.height(14.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Obsidian)
            .border(1.dp, BorderObsidian, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Achievements", style = MaterialTheme.typography.titleMedium, color = SoftFrost)
        SampleProfile.achievements.forEach { achievement ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (achievement.unlocked) WarmAmber.copy(alpha = 0.25f) else BorderObsidian),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (achievement.unlocked) "★" else "·",
                        color = if (achievement.unlocked) WarmAmber else SoftFrostMuted,
                    )
                }
                Column {
                    Text(achievement.title, style = MaterialTheme.typography.titleMedium, color = SoftFrost)
                    Text(achievement.subtitle, style = MaterialTheme.typography.bodyMedium, color = SoftFrostMuted)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Obsidian)
            .border(1.dp, BorderObsidian, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.titleMedium, color = SoftFrost)
        SettingRow("Monitored apps", "Instagram, YouTube, Reddit")
        SettingRow("Sensitivity", "Medium · 8 min dwell")
        SettingRow("Quiet hours", "11:00 PM – 7:00 AM")
        SettingRow("Preferred categories", "Movement, Mindfulness")
    }
}

@Composable
private fun SquadsTabContent() {
    Text(
        text = "Join squads for shared accountability and joint challenges.",
        style = MaterialTheme.typography.bodyMedium,
        color = SoftFrostMuted,
    )
    Spacer(modifier = Modifier.height(12.dp))

    SampleProfile.squads.forEach { squad ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Obsidian)
                .border(1.dp, BorderObsidian, RoundedCornerShape(20.dp))
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(squad.name, style = MaterialTheme.typography.titleMedium, color = SoftFrost)
                Text("${squad.members} members", style = MaterialTheme.typography.labelMedium, color = SoftFrostMuted)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(squad.challenge, style = MaterialTheme.typography.bodyMedium, color = SoftFrostMuted)
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { squad.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = SereneMint,
                trackColor = BorderObsidian,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${(squad.progress * 100).toInt()}% squad progress",
                style = MaterialTheme.typography.labelMedium,
                color = SereneMint,
            )
        }
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = SoftFrost)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = SoftFrostMuted)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ProfileScreenPreview() {
    BreakTheScrollTheme { ProfileScreen() }
}
