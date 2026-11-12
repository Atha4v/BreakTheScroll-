package com.breakthescroll.app.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PhoneDisabled
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity

enum class FeedMode {
    ForYou,
    Serendipity, // UI label: Random
}

enum class BuddyMood {
    Idle,
    Working,
    Cheer,
    Rest,
    Nudge,
}

enum class BuddyScreenContext {
    Activities,
    Habits,
    Progress,
    Groups,
}

enum class ActivityIllustration {
    Walk,
    Bike,
    Yoga,
    Breath,
    Hydrate,
    Read,
    Tea,
    PhoneDown,
    Social,
    Window,
}

data class InterestOption(
    val tag: String,
    val subtitle: String,
    val icon: ImageVector,
)

val InterestUiOptions: List<InterestOption> = listOf(
    InterestOption("Coding", "Repos, bugs, tiny builds", Icons.Outlined.Code),
    InterestOption("Sports", "Move, play, sweat a little", Icons.Outlined.SportsSoccer),
    InterestOption("Art", "Sketch, color, make stuff", Icons.Outlined.Brush),
    InterestOption("Reading", "Pages over feeds", Icons.AutoMirrored.Outlined.MenuBook),
    InterestOption("Fitness", "Strength & mobility", Icons.Outlined.FitnessCenter),
    InterestOption("Music", "One track, full attention", Icons.Outlined.MusicNote),
    InterestOption("Mindfulness", "Breathe, notice, reset", Icons.Outlined.SelfImprovement),
)

data class ActivityChallenge(
    val id: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val category: String,
    val difficulty: Int,
    val icon: ImageVector,
    val illustration: ActivityIllustration,
    val heroColors: List<Color>,
    val isPersonalized: Boolean,
    val imageUrl: String,
    val interestTag: String,
    val durationSeconds: Int,
    val source: ActivityCatalogEntity,
)

fun ActivityCatalogEntity.toChallenge(): ActivityChallenge {
    val kind = runCatching { ActivityIllustration.valueOf(illustration) }
        .getOrDefault(ActivityIllustration.Walk)
    return ActivityChallenge(
        id = id,
        title = title,
        description = description,
        durationMinutes = durationMinutes,
        category = category,
        difficulty = difficulty,
        icon = iconFor(kind),
        illustration = kind,
        heroColors = listOf(
            Color(heroColorStart.toLong() and 0xFFFFFFFFL),
            Color(heroColorEnd.toLong() and 0xFFFFFFFFL),
        ),
        isPersonalized = isPersonalized,
        imageUrl = imageUrl,
        interestTag = interestTag,
        durationSeconds = durationSeconds.coerceAtLeast(15),
        source = this,
    )
}

private fun iconFor(kind: ActivityIllustration): ImageVector = when (kind) {
    ActivityIllustration.Walk -> Icons.AutoMirrored.Outlined.DirectionsWalk
    ActivityIllustration.Bike -> Icons.AutoMirrored.Outlined.DirectionsBike
    ActivityIllustration.Yoga, ActivityIllustration.Breath -> Icons.Outlined.SelfImprovement
    ActivityIllustration.Hydrate -> Icons.Outlined.WaterDrop
    ActivityIllustration.Read -> Icons.AutoMirrored.Outlined.MenuBook
    ActivityIllustration.Tea -> Icons.Outlined.LocalCafe
    ActivityIllustration.PhoneDown -> Icons.Outlined.PhoneDisabled
    ActivityIllustration.Social, ActivityIllustration.Window -> Icons.Outlined.Spa
}
