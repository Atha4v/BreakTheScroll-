package com.breakthescroll.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.FeedMode
import com.breakthescroll.app.ui.model.SampleActivityCatalog
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.SurfaceNeutral

@Composable
fun ActivitiesScreen(
    modifier: Modifier = Modifier,
    onAccepted: (ActivityChallenge) -> Unit = {},
    onPassed: (ActivityChallenge) -> Unit = {},
) {
    var feedMode by remember { mutableStateOf(FeedMode.ForYou) }
    val challenges = remember(feedMode) { SampleActivityCatalog.forMode(feedMode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceNeutral)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.activities_title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (feedMode) {
                FeedMode.ForYou -> "Personalized micro-tasks matched to your filters."
                FeedMode.Serendipity -> "Stochastic challenges to break autopilot scrolling."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeedModeToggle(
            selected = feedMode,
            onSelected = { feedMode = it },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        ActivityFlashCardDeck(
            challenges = challenges,
            onAccepted = onAccepted,
            onPassed = onPassed,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 840)
@Composable
private fun ActivitiesScreenPreview() {
    BreakTheScrollTheme {
        ActivitiesScreen()
    }
}
