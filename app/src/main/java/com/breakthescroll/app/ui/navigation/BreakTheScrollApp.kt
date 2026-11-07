package com.breakthescroll.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.components.navigation.PulsingStartFab
import com.breakthescroll.app.ui.components.navigation.StartResetSheet
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.screens.ActivitiesScreen
import com.breakthescroll.app.ui.screens.HabitsScreen
import com.breakthescroll.app.ui.screens.ProfileScreen
import com.breakthescroll.app.ui.screens.ProgressScreen
import com.breakthescroll.app.ui.theme.DeepSlate
import com.breakthescroll.app.ui.theme.Obsidian
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.SoftFrostMuted

private enum class AppDestination(
    val labelRes: Int,
    val icon: ImageVector?,
) {
    Activities(R.string.nav_activities, Icons.Outlined.Home),
    Habits(R.string.nav_habits, Icons.Outlined.Spa),
    Start(R.string.nav_start, null),
    Progress(R.string.nav_progress, Icons.Outlined.AutoGraph),
    Profile(R.string.nav_profile, Icons.Outlined.Person),
}

@Composable
fun BreakTheScrollApp(
    onAcceptedToast: (String) -> Unit = {},
    onPassedToast: (String) -> Unit = {},
) {
    var destination by remember { mutableStateOf(AppDestination.Activities) }
    var buddyMood by remember { mutableStateOf(BuddyMood.Idle) }
    var showStartSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepSlate,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = Obsidian,
                contentColor = SoftFrost,
                tonalElevation = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
            ) {
                AppDestination.entries.forEach { item ->
                    if (item == AppDestination.Start) {
                        NavigationBarItem(
                            selected = false,
                            onClick = { showStartSheet = true },
                            icon = {
                                PulsingStartFab(onClick = { showStartSheet = true })
                            },
                            label = {
                                Text(stringResource(item.labelRes), color = SereneMint)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = DeepSlate.copy(alpha = 0f),
                            ),
                        )
                    } else {
                        NavigationBarItem(
                            selected = destination == item,
                            onClick = { destination = item },
                            icon = {
                                Icon(
                                    imageVector = item.icon!!,
                                    contentDescription = stringResource(item.labelRes),
                                )
                            },
                            label = { Text(stringResource(item.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SereneMint,
                                selectedTextColor = SereneMint,
                                unselectedIconColor = SoftFrostMuted,
                                unselectedTextColor = SoftFrostMuted,
                                indicatorColor = SereneMint.copy(alpha = 0.14f),
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .statusBarsPadding()
        when (destination) {
            AppDestination.Activities -> ActivitiesScreen(
                buddyMood = buddyMood,
                modifier = contentModifier,
                onAccepted = {
                    buddyMood = BuddyMood.Cheer
                    onAcceptedToast(it.title)
                },
                onPassed = {
                    buddyMood = BuddyMood.Nudge
                    onPassedToast(it.title)
                },
            )
            AppDestination.Habits -> HabitsScreen(modifier = contentModifier)
            AppDestination.Start -> ActivitiesScreen(buddyMood = buddyMood, modifier = contentModifier)
            AppDestination.Progress -> ProgressScreen(modifier = contentModifier)
            AppDestination.Profile -> ProfileScreen(modifier = contentModifier)
        }
    }

    if (showStartSheet) {
        StartResetSheet(
            onDismiss = { showStartSheet = false },
            onComplete = {
                showStartSheet = false
                buddyMood = BuddyMood.Cheer
                destination = AppDestination.Activities
            },
        )
    }
}
