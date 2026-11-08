package com.breakthescroll.app.ui.components.activitydeck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.model.FeedMode
import com.breakthescroll.app.ui.theme.LocalBtsPalette

@Composable
fun FeedModeToggle(
    selected: FeedMode,
    onSelected: (FeedMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    val modes = FeedMode.entries

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = selected == mode,
                onClick = { onSelected(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                border = BorderStroke(1.dp, palette.border),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = palette.primaryContainer,
                    activeContentColor = palette.primary,
                    inactiveContainerColor = palette.surface,
                    inactiveContentColor = palette.onSurfaceMuted,
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
