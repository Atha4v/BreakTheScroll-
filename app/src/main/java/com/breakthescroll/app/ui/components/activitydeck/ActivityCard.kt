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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.SampleActivityCatalog
import com.breakthescroll.app.ui.theme.AcceptTint
import com.breakthescroll.app.ui.theme.BorderSubtle
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.ContainerWhite
import com.breakthescroll.app.ui.theme.PassCoral
import com.breakthescroll.app.ui.theme.PrimaryEmerald
import com.breakthescroll.app.ui.theme.PrimaryEmeraldContainer
import com.breakthescroll.app.ui.theme.SecondaryAmber
import com.breakthescroll.app.ui.theme.SecondaryAmberContainer

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
        colors = CardDefaults.cardColors(containerColor = ContainerWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, BorderSubtle),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryEmeraldContainer,
                    ) {
                        Text(
                            text = challenge.category.label.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryEmerald,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryAmberContainer,
                    ) {
                        Text(
                            text = "${challenge.durationMinutes} min",
                            style = MaterialTheme.typography.labelMedium,
                            color = SecondaryAmber,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryEmeraldContainer, Color(0xFFECFDF5)),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = challenge.icon,
                        contentDescription = null,
                        tint = PrimaryEmerald,
                        modifier = Modifier.size(36.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.weight(1f))

                DifficultyDots(level = challenge.difficulty)
            }

            SwipeDecisionStamp(
                label = "ACCEPT",
                color = AcceptTint,
                alpha = acceptAlpha,
                alignment = Alignment.TopStart,
                rotation = -12f,
                modifier = Modifier.padding(28.dp),
            )
            SwipeDecisionStamp(
                label = "PASS",
                color = PassCoral,
                alpha = passAlpha,
                alignment = Alignment.TopEnd,
                rotation = 12f,
                modifier = Modifier.padding(28.dp),
            )
        }
    }
}

@Composable
private fun DifficultyDots(level: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < level) PrimaryEmerald else BorderSubtle,
                    ),
            )
        }
        Text(
            text = "Effort",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Preview(showBackground = true, widthDp = 360, heightDp = 480)
@Composable
private fun ActivityCardPreview() {
    BreakTheScrollTheme {
        ActivityCard(
            challenge = SampleActivityCatalog.forYou.first(),
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .padding(16.dp),
            swipeProgress = 0.35f,
        )
    }
}
