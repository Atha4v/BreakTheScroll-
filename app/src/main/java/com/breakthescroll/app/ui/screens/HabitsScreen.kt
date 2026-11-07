package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.model.HabitItem
import com.breakthescroll.app.ui.model.SampleHabits
import com.breakthescroll.app.ui.theme.BorderObsidian
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.DeepSlate
import com.breakthescroll.app.ui.theme.Obsidian
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.SoftFrostMuted
import com.breakthescroll.app.ui.theme.WarmAmber

@Composable
fun HabitsScreen(modifier: Modifier = Modifier) {
    var habits by remember { mutableStateOf(SampleHabits.items) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.habits_title),
                style = MaterialTheme.typography.displaySmall,
                color = SoftFrost,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Build tiny offline routines Buddy can celebrate with you.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftFrostMuted,
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        onToggle = {
                            habits = habits.map {
                                if (it.id == habit.id) {
                                    it.copy(
                                        completedToday = !it.completedToday,
                                        streakDays = if (!it.completedToday) it.streakDays + 1 else it.streakDays,
                                    )
                                } else {
                                    it
                                }
                            }
                        },
                    )
                }
                item { Spacer(modifier = Modifier.height(96.dp)) }
            }
        }

        FloatingActionButton(
            onClick = {
                habits = habits + HabitItem(
                    id = "h${habits.size + 1}",
                    title = "New micro-habit",
                    cadence = "Custom · edit me",
                    streakDays = 0,
                    completedToday = false,
                    accent = SereneMint,
                )
            },
            containerColor = SereneMint,
            contentColor = DeepSlate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Add habit")
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitItem,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Obsidian)
            .border(1.dp, BorderObsidian, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(habit.accent.copy(alpha = 0.18f))
                .border(1.dp, habit.accent.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${habit.streakDays}",
                style = MaterialTheme.typography.titleMedium,
                color = habit.accent,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(habit.title, style = MaterialTheme.typography.titleMedium, color = SoftFrost)
            Spacer(modifier = Modifier.height(2.dp))
            Text(habit.cadence, style = MaterialTheme.typography.bodyMedium, color = SoftFrostMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (habit.completedToday) "Done today" else "Pending · keep the chain",
                style = MaterialTheme.typography.labelMedium,
                color = if (habit.completedToday) SereneMint else WarmAmber,
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (habit.completedToday) SereneMint else Obsidian)
                .border(1.dp, if (habit.completedToday) SereneMint else BorderObsidian, CircleShape)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Toggle habit",
                tint = if (habit.completedToday) DeepSlate else SoftFrostMuted,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun HabitsScreenPreview() {
    BreakTheScrollTheme { HabitsScreen() }
}
