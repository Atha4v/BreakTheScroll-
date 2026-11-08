package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
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
    buddyMood: BuddyMood,
    feedMode: FeedMode,
    catalog: List<ActivityCatalogEntity>,
    onFeedModeChange: (FeedMode) -> Unit,
    onAccepted: (ActivityChallenge) -> Unit,
    onPassed: (ActivityChallenge) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val challenges = remember(catalog) { catalog.map { it.toChallenge() } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.activities_title),
            style = MaterialTheme.typography.displaySmall,
            color = palette.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (feedMode) {
                FeedMode.ForYou -> "Personalized micro-tasks matched to your filters."
                FeedMode.Serendipity -> "Stochastic challenges to break autopilot scrolling."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = palette.onSurfaceMuted,
        )

        Spacer(modifier = Modifier.height(16.dp))
        ScrollBuddyBanner(mood = buddyMood)
        Spacer(modifier = Modifier.height(16.dp))
        FeedModeToggle(
            selected = feedMode,
            onSelected = onFeedModeChange,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        ActivityFlashCardDeck(
            challenges = challenges,
            onAccepted = onAccepted,
            onPassed = onPassed,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(88.dp))
    }
}
