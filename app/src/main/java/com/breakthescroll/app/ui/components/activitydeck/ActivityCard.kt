package com.breakthescroll.app.ui.components.activitydeck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.ui.components.illustrations.ActivityHeroIllustration
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.FeedMode
import com.breakthescroll.app.ui.model.SampleActivityCatalog
import com.breakthescroll.app.ui.theme.AcceptTint
import com.breakthescroll.app.ui.theme.BorderObsidian
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.Obsidian
import com.breakthescroll.app.ui.theme.PassCoral
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.SereneMintContainer
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.SoftFrostMuted
import com.breakthescroll.app.ui.theme.WarmAmber
import com.breakthescroll.app.ui.theme.WarmAmberContainer

@Composable
fun ActivityCard(
    challenge: ActivityChallenge,
    modifier: Modifier = Modifier,
    swipeProgress: Float = 0f,
) {
    val acceptAlpha = swipeProgress.coerceIn(0f, 1f)
    val passAlpha = (-swipeProgress).coerceIn(0f, 1f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Obsidian),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, BorderObsidian),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                ) {
                    ActivityHeroIllustration(
                        kind = challenge.illustration,
                        heroColors = challenge.heroColors,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SereneMintContainer.copy(alpha = 0.92f),
                        ) {
                            Text(
                                text = challenge.category.label.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = SereneMint,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = WarmAmberContainer.copy(alpha = 0.92f),
                        ) {
                            Text(
                                text = "${challenge.durationMinutes} min",
                                style = MaterialTheme.typography.labelMedium,
                                color = WarmAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SereneMintContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = challenge.icon,
                                contentDescription = null,
                                tint = SereneMint,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = SoftFrost,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftFrostMuted,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    DifficultyDots(level = challenge.difficulty)
                }
            }

            SwipeDecisionStamp(
                label = "ACCEPT",
                color = AcceptTint,
                alpha = acceptAlpha,
                alignment = Alignment.CenterStart,
                rotation = -12f,
                modifier = Modifier.padding(24.dp),
            )
            SwipeDecisionStamp(
                label = "PASS",
                color = PassCoral,
                alpha = passAlpha,
                alignment = Alignment.CenterEnd,
                rotation = 12f,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

@Composable
private fun DifficultyDots(level: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (index < level) SereneMint else BorderObsidian),
            )
        }
        Text(
            text = "Effort",
            style = MaterialTheme.typography.labelMedium,
            color = SoftFrostMuted,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun SwipeDecisionStamp(
    label: String,
    color: Color,
    alpha: Float,
    alignment: Alignment,
    rotation: Float,
    modifier: Modifier = Modifier,
) {
    if (alpha <= 0.01f) return

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = color.copy(alpha = alpha),
            modifier = modifier.graphicsLayer {
                this.alpha = alpha
                rotationZ = rotation
                scaleX = 0.92f + (0.08f * alpha)
                scaleY = 0.92f + (0.08f * alpha)
            },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, widthDp = 360, heightDp = 480)
@Composable
private fun ActivityCardPreview() {
    BreakTheScrollTheme {
        ActivityCard(
            challenge = SampleActivityCatalog.forMode(FeedMode.ForYou).first(),
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .padding(16.dp),
            swipeProgress = 0.35f,
        )
    }
}
