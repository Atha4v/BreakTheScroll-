package com.breakthescroll.app.ui.components.activitydeck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.model.FeedMode
import com.breakthescroll.app.ui.theme.BorderSubtle
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.ContainerWhite
import com.breakthescroll.app.ui.theme.PrimaryEmerald
import com.breakthescroll.app.ui.theme.PrimaryEmeraldContainer

@Composable
fun FeedModeToggle(
    selected: FeedMode,
    onSelected: (FeedMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = FeedMode.entries

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = selected == mode,
                onClick = { onSelected(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                border = BorderStroke(1.dp, BorderSubtle),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = PrimaryEmeraldContainer,
                    activeContentColor = PrimaryEmerald,
                    inactiveContainerColor = ContainerWhite,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                label = {
                    Text(
                        text = when (mode) {
                            FeedMode.ForYou -> stringResource(R.string.feed_for_you)
                            FeedMode.Serendipity -> stringResource(R.string.feed_serendipity)
                        },
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedModeTogglePreview() {
    BreakTheScrollTheme {
        FeedModeToggle(
            selected = FeedMode.ForYou,
            onSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
