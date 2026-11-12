package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.ui.components.activitydeck.ActivityFlashCardDeck
import com.breakthescroll.app.ui.components.activitydeck.FeedModeToggle
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.FeedMode
import com.breakthescroll.app.ui.model.toChallenge
import com.breakthescroll.app.ui.theme.LocalBtsPalette

@Composable
fun ActivitiesScreen(
    displayName: String,
    buddyMood: BuddyMood,
    buddyMessage: String,
    feedMode: FeedMode,
    catalog: List<ActivityCatalogEntity>,
    engagementLocked: Boolean = false,
    completedChallengeId: String? = null,
    onCompletedConsumed: () -> Unit = {},
    onFeedModeChange: (FeedMode) -> Unit,
    onAccepted: (ActivityChallenge) -> Unit,
    onPassed: (ActivityChallenge) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val challenges = remember(catalog) { catalog.map { it.toChallenge() } }
    val greeting = remember(displayName) {
        val first = displayName.trim().ifBlank { "there" }.substringBefore(' ')
        "Hey, $first"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge,
            color = palette.onBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        ScrollBuddyBanner(mood = buddyMood, customMessage = buddyMessage)
        Spacer(modifier = Modifier.height(10.dp))
        FeedModeToggle(
            selected = feedMode,
            onSelected = onFeedModeChange,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        ActivityFlashCardDeck(
            challenges = challenges,
            engagementLocked = engagementLocked,
            completedChallengeId = completedChallengeId,
            onCompletedConsumed = onCompletedConsumed,
            onAccepted = onAccepted,
            onPassed = onPassed,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}
