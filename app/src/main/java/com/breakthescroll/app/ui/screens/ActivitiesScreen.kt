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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.components.activitydeck.ActivityFlashCardDeck
import com.breakthescroll.app.ui.components.activitydeck.FeedModeToggle
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.FeedMode
import com.breakthescroll.app.ui.model.SampleActivityCatalog
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.DeepSlate
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.SoftFrostMuted

@Composable
fun ActivitiesScreen(
    buddyMood: BuddyMood,
    modifier: Modifier = Modifier,
    onAccepted: (ActivityChallenge) -> Unit = {},
    onPassed: (ActivityChallenge) -> Unit = {},
) {
    var feedMode by remember { mutableStateOf(FeedMode.ForYou) }
    val challenges = remember(feedMode) { SampleActivityCatalog.forMode(feedMode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSlate)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.activities_title),
            style = MaterialTheme.typography.displaySmall,
            color = SoftFrost,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (feedMode) {
                FeedMode.ForYou -> "Personalized micro-tasks matched to your filters."
                FeedMode.Serendipity -> "Stochastic challenges to break autopilot scrolling."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = SoftFrostMuted,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScrollBuddyBanner(mood = buddyMood)

        Spacer(modifier = Modifier.height(16.dp))

        FeedModeToggle(
            selected = feedMode,
            onSelected = { feedMode = it },
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

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, widthDp = 390, heightDp = 900)
@Composable
private fun ActivitiesScreenPreview() {
    BreakTheScrollTheme {
        ActivitiesScreen(buddyMood = BuddyMood.Idle)
    }
}
