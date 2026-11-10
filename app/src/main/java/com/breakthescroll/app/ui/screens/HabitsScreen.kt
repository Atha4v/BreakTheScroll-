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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import java.time.LocalDate

@Composable
fun HabitsScreen(
    habits: List<HabitEntity>,
    buddyMood: BuddyMood,
    buddyMessage: String,
    onAddHabit: (title: String, cadence: String) -> Unit,
    onToggleHabit: (HabitEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var cadence by remember { mutableStateOf("Daily") }
    val today = LocalDate.now().toEpochDay()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.habits_title),
                style = MaterialTheme.typography.displaySmall,
                color = palette.onBackground,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Check off a habit — it syncs into Progress with a timestamp.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.onSurfaceMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))
            ScrollBuddyBanner(mood = buddyMood, customMessage = buddyMessage)
            Spacer(modifier = Modifier.height(16.dp))

            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                ) {
                    Text(
                        text = "No habits yet. Tap + to build your first offline routine.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceMuted,
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(habits, key = { it.id }) { habit ->
                        val doneToday = habit.completedToday && habit.lastCompletedEpochDay == today
                        HabitCard(
                            habit = habit,
                            doneToday = doneToday,
                            onToggle = { onToggleHabit(habit) },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(96.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = palette.primary,
            contentColor = palette.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Add habit")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New habit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = cadence,
                        onValueChange = { cadence = it },
                        label = { Text("Cadence") },
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAddHabit(title, cadence)
                        title = ""
                        cadence = "Daily"
                        showDialog = false
                    },
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun HabitCard(
    habit: HabitEntity,
    doneToday: Boolean,
    onToggle: () -> Unit,
) {
    val palette = LocalBtsPalette.current
    val accent = Color(habit.accentArgb.toLong() and 0xFFFFFFFFL)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f))
                .border(1.dp, accent.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (habit.streakDays > 0) "🔥${habit.streakDays}" else "0",
                style = MaterialTheme.typography.titleSmall,
                color = accent,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(habit.title, style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
            Text(habit.cadence, style = MaterialTheme.typography.bodyMedium, color = palette.onSurfaceMuted)
            Text(
                text = if (doneToday) "Done today" else "Pending · keep the chain",
                style = MaterialTheme.typography.labelMedium,
                color = if (doneToday) palette.primary else palette.highlight,
            )
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (doneToday) palette.primary else palette.surface)
                .border(1.dp, if (doneToday) palette.primary else palette.border, CircleShape)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Toggle habit",
                tint = if (doneToday) palette.onPrimary else palette.onSurfaceMuted,
            )
        }
    }
}
