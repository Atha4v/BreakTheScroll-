package com.breakthescroll.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.components.engagement.EngagementTimerModal
import com.breakthescroll.app.ui.components.navigation.PulsingStartFab
import com.breakthescroll.app.ui.components.navigation.StartResetSheet
import com.breakthescroll.app.ui.model.BuddyScreenContext
import com.breakthescroll.app.ui.model.FeedMode
import com.breakthescroll.app.ui.screens.ActivitiesScreen
import com.breakthescroll.app.ui.screens.GroupsScreen
import com.breakthescroll.app.ui.screens.HabitsScreen
import com.breakthescroll.app.ui.screens.OnboardingScreen
import com.breakthescroll.app.ui.screens.PreferencesDialog
import com.breakthescroll.app.ui.screens.ProgressScreen
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import com.breakthescroll.app.ui.viewmodel.AppViewModel

private enum class AppDestination(
    val labelRes: Int,
    val icon: ImageVector?,
) {
    Activities(R.string.nav_activities, Icons.Outlined.Home),
    Habits(R.string.nav_habits, Icons.Outlined.Spa),
    Start(R.string.nav_start, null),
    Progress(R.string.nav_progress, Icons.Outlined.AutoGraph),
    Groups(R.string.nav_groups, Icons.Outlined.Groups),
}

@Composable
fun BreakTheScrollApp(
    viewModel: AppViewModel,
    onAcceptedToast: (String) -> Unit = {},
    onPassedToast: (String) -> Unit = {},
) {
    val palette = LocalBtsPalette.current
    var destination by remember { mutableStateOf(AppDestination.Activities) }
    var showStartSheet by remember { mutableStateOf(false) }
    var editingInterests by remember { mutableStateOf(false) }

    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
    val buddyMood by viewModel.buddyMood.collectAsStateWithLifecycle()
    val feedMode by viewModel.feedMode.collectAsStateWithLifecycle()
    val hybridDeck by viewModel.hybridDeck.collectAsStateWithLifecycle()
    val serendipity by viewModel.serendipity.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val displayName by viewModel.displayName.collectAsStateWithLifecycle()
    val onboardingComplete by viewModel.onboardingComplete.collectAsStateWithLifecycle()
    val interests by viewModel.interests.collectAsStateWithLifecycle()
    val posts by viewModel.communityPosts.collectAsStateWithLifecycle()
    val myGroups by viewModel.myGroups.collectAsStateWithLifecycle()
    val discoverGroups by viewModel.discoverGroups.collectAsStateWithLifecycle()
    val groupSearch by viewModel.groupSearch.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val pendingEngagement by viewModel.pendingEngagement.collectAsStateWithLifecycle()
    val completedChallengeId by viewModel.completedChallengeId.collectAsStateWithLifecycle()
    val showPreferences by viewModel.showPreferences.collectAsStateWithLifecycle()

    val catalog = if (feedMode == FeedMode.Hybrid) hybridDeck else serendipity

    if (!onboardingComplete || editingInterests) {
        OnboardingScreen(
            initialInterests = interests,
            isEditing = editingInterests || onboardingComplete,
            onComplete = { selected ->
                if (editingInterests) {
                    viewModel.updateInterests(selected)
                    editingInterests = false
                } else {
                    viewModel.completeOnboarding(selected)
                }
            },
            onSkipOrDismiss = if (editingInterests) {
                {
                    editingInterests = false
                    viewModel.dismissPreferences()
                }
            } else {
                null
            },
        )
        return
    }

    Scaffold(
        containerColor = palette.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = palette.surface,
                contentColor = palette.onBackground,
                tonalElevation = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
            ) {
                AppDestination.entries.forEach { item ->
                    if (item == AppDestination.Start) {
                        NavigationBarItem(
                            selected = false,
                            onClick = { showStartSheet = true },
                            icon = { PulsingStartFab(onClick = { showStartSheet = true }) },
                            label = {
                                Text(stringResource(item.labelRes), color = palette.primary)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = palette.background.copy(alpha = 0f),
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
                                selectedIconColor = palette.primary,
                                selectedTextColor = palette.primary,
                                unselectedIconColor = palette.onSurfaceMuted,
                                unselectedTextColor = palette.onSurfaceMuted,
                                indicatorColor = palette.primary.copy(alpha = 0.14f),
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
            AppDestination.Activities, AppDestination.Start -> ActivitiesScreen(
                displayName = displayName,
                buddyMood = buddyMood,
                buddyMessage = viewModel.buddyMessage(BuddyScreenContext.Activities),
                feedMode = feedMode,
                catalog = catalog,
                engagementLocked = pendingEngagement != null,
                completedChallengeId = completedChallengeId,
                onCompletedConsumed = viewModel::consumeCompletedChallengeId,
                onFeedModeChange = viewModel::setFeedMode,
                onAccepted = { challenge ->
                    viewModel.requestEngagement(challenge)
                },
                onPassed = {
                    viewModel.passActivity()
                    onPassedToast(it.title)
                },
                modifier = contentModifier,
            )
            AppDestination.Habits -> HabitsScreen(
                habits = habits,
                buddyMood = buddyMood,
                buddyMessage = viewModel.buddyMessage(BuddyScreenContext.Habits),
                onAddHabit = viewModel::addHabit,
                onToggleHabit = viewModel::toggleHabit,
                modifier = contentModifier,
            )
            AppDestination.Progress -> ProgressScreen(
                state = progress,
                buddyMood = buddyMood,
                buddyMessage = viewModel.buddyMessage(BuddyScreenContext.Progress),
                modifier = contentModifier,
            )
            AppDestination.Groups -> GroupsScreen(
                posts = posts,
                myGroups = myGroups,
                discoverGroups = discoverGroups,
                searchQuery = groupSearch,
                username = username,
                recentCompletions = progress.recent,
                buddyMood = buddyMood,
                buddyMessage = viewModel.buddyMessage(BuddyScreenContext.Groups),
                onSearchChange = viewModel::setGroupSearch,
                onCheer = viewModel::toggleCheer,
                onShareCompletion = { completion, note ->
                    viewModel.shareCompletion(completion, note)
                },
                onCreateGroup = viewModel::createGroup,
                onAddMember = viewModel::addMember,
                onJoinGroup = viewModel::joinGroup,
                onOpenPreferences = viewModel::openPreferences,
                modifier = contentModifier,
            )
        }
    }

    if (showStartSheet) {
        StartResetSheet(
            onDismiss = { showStartSheet = false },
            onComplete = {
                showStartSheet = false
                viewModel.completeReset()
                destination = AppDestination.Activities
            },
        )
    }

    pendingEngagement?.let { challenge ->
        EngagementTimerModal(
            challenge = challenge,
            currentStreak = stats.currentStreak,
            onCompleted = {
                viewModel.confirmEngagement()
                onAcceptedToast(challenge.title)
            },
            onDismiss = viewModel::cancelEngagement,
        )
    }

    if (showPreferences && !editingInterests) {
        PreferencesDialog(
            displayName = displayName,
            stats = stats,
            darkTheme = darkTheme,
            interests = interests,
            onDarkThemeChange = viewModel::setDarkTheme,
            onEditInterests = {
                viewModel.dismissPreferences()
                editingInterests = true
            },
            onDismiss = viewModel::dismissPreferences,
        )
    }
}
