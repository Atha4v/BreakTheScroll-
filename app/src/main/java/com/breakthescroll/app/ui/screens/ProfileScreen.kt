package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.breakthescroll.app.data.local.entity.UserStatsEntity
import com.breakthescroll.app.ui.theme.LocalBtsPalette

@Composable
fun PreferencesDialog(
    displayName: String,
    stats: UserStatsEntity,
    darkTheme: Boolean,
    interests: Set<String>,
    onDarkThemeChange: (Boolean) -> Unit,
    onEditInterests: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = LocalBtsPalette.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(displayName, style = MaterialTheme.typography.headlineMedium, color = palette.onBackground)
            Text(
                "🔥 ${stats.currentStreak}-day streak · ${stats.tasksCompleted} timed wins",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.onSurfaceMuted,
            )

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
                        if (darkTheme) "Deep Slate · Serene Mint" else "Pristine White · Resilient Copper",
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.background)
                    .padding(14.dp),
            ) {
                Text("Interests", style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (interests.isEmpty()) "None selected yet" else interests.joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.onSurfaceMuted,
                )
                TextButton(onClick = onEditInterests) {
                    Text("Edit interest mix", color = palette.primary)
                }
            }

            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("Done", color = palette.primary)
            }
        }
    }
}
