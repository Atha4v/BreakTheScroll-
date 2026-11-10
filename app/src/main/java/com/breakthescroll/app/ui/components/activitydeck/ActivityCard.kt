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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.breakthescroll.app.ui.components.illustrations.ActivityHeroIllustration
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.theme.LocalBtsPalette

@Composable
fun ActivityCard(
    challenge: ActivityChallenge,
    modifier: Modifier = Modifier,
    swipeProgress: Float = 0f,
) {
    val palette = LocalBtsPalette.current
    val acceptAlpha = swipeProgress.coerceIn(0f, 1f)
    val passAlpha = (-swipeProgress).coerceIn(0f, 1f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, palette.border),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                ) {
                    if (challenge.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = challenge.imageUrl,
                            contentDescription = challenge.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            palette.surface.copy(alpha = 0.55f),
                                        ),
                                    ),
                                ),
                        )
                    } else {
                        ActivityHeroIllustration(
                            kind = challenge.illustration,
                            heroColors = challenge.heroColors,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.primaryContainer.copy(alpha = 0.92f),
                        ) {
                            Text(
                                text = challenge.category.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.highlightContainer.copy(alpha = 0.92f),
                        ) {
                            Text(
                                text = formatEngageLabel(challenge.durationSeconds),
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.highlight,
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
                                .background(palette.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = challenge.icon,
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = palette.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceMuted,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index < challenge.difficulty) palette.primary else palette.border,
                                    ),
                            )
                        }
                        Text(
                            text = if (challenge.isPersonalized) "For you" else "Serendipity",
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.onSurfaceMuted,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }

            SwipeStamp("ENGAGE", palette.primary, acceptAlpha, Alignment.CenterStart, -12f)
            SwipeStamp("PASS", palette.error, passAlpha, Alignment.CenterEnd, 12f)
        }
    }
}

private fun formatEngageLabel(durationSeconds: Int): String {
    return if (durationSeconds < 60) {
        "${durationSeconds}s timer"
    } else {
        val m = durationSeconds / 60
        val s = durationSeconds % 60
        if (s == 0) "${m}m timer" else "${m}m ${s}s timer"
    }
}

@Composable
private fun SwipeStamp(
    label: String,
    color: Color,
    alpha: Float,
    alignment: Alignment,
    rotation: Float,
) {
    if (alpha <= 0.01f) return
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = color.copy(alpha = alpha),
            modifier = Modifier
                .padding(24.dp)
                .graphicsLayer {
                    this.alpha = alpha
                    rotationZ = rotation
                    scaleX = 0.92f + 0.08f * alpha
                    scaleY = 0.92f + 0.08f * alpha
                },
        )
    }
}
