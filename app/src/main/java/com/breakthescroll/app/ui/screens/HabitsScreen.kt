package com.breakthescroll.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import java.time.LocalDate

private val CadenceOptions = listOf("Daily", "Weekdays", "Weekly")

@OptIn(ExperimentalMaterial3Api::class)
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
    var showSheet by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var cadence by remember { mutableStateOf("Daily") }
    val today = LocalDate.now().toEpochDay()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            Spacer(modifier = Modifier.height(12.dp))
            ScrollBuddyBanner(mood = buddyMood, customMessage = buddyMessage)
            Spacer(modifier = Modifier.height(14.dp))

            if (habits.isEmpty()) {
                Text(
                    text = "Tap + to begin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceMuted,
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
            onClick = { showSheet = true },
            containerColor = palette.primary,
            contentColor = palette.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Add habit")
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = palette.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    "New habit",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = palette.onBackground,
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Name") },
                    placeholder = { Text("e.g. Morning stretch") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = habitFieldColors(),
                )
                Text(
                    "Cadence",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.onSurfaceMuted,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CadenceOptions.forEach { option ->
                        FilterChip(
                            selected = cadence == option,
                            onClick = { cadence = option },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = palette.primaryContainer,
                                selectedLabelColor = palette.primary,
                            ),
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { showSheet = false }) {
                        Text("Cancel", color = palette.onSurfaceMuted)
                    }
                    TextButton(
                        onClick = {
                            onAddHabit(title.trim(), cadence)
                            title = ""
                            cadence = "Daily"
                            showSheet = false
                        },
                        enabled = title.isNotBlank(),
                    ) {
                        Text("Save", color = palette.primary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun habitFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LocalBtsPalette.current.primary,
    unfocusedBorderColor = LocalBtsPalette.current.border,
    focusedContainerColor = LocalBtsPalette.current.background,
    unfocusedContainerColor = LocalBtsPalette.current.background,
)

@Composable
private fun HabitCard(
    habit: HabitEntity,
    doneToday: Boolean,
    onToggle: () -> Unit,
) {
    val palette = LocalBtsPalette.current
    val accent = Color(habit.accentArgb.toLong() and 0xFFFFFFFFL)
    val checkBg by animateColorAsState(
        if (doneToday) palette.primary else palette.surface,
        label = "checkBg",
    )
    val cardAlpha by animateFloatAsState(
        if (doneToday) 0.72f else 1f,
        label = "habitAlpha",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = cardAlpha }
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (habit.streakDays > 0) "🔥${habit.streakDays}" else "·",
                style = MaterialTheme.typography.labelLarge,
                color = accent,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(habit.title, style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
            Text(
                if (doneToday) "Done · ${habit.cadence}" else habit.cadence,
                style = MaterialTheme.typography.bodyMedium,
                color = if (doneToday) palette.primary else palette.onSurfaceMuted,
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(checkBg)
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
