package com.breakthescroll.app.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.PhoneDisabled
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.ui.graphics.vector.ImageVector

enum class FeedMode {
    ForYou,
    Serendipity,
}

enum class WellnessCategory(val label: String) {
    Movement("Movement"),
    Mindfulness("Mindfulness"),
    Social("Social"),
    DigitalDetox("Digital Detox"),
    Nourish("Nourish"),
}

data class ActivityChallenge(
    val id: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val category: WellnessCategory,
    val difficulty: Int = 1,
    val icon: ImageVector,
    val isPersonalized: Boolean = true,
)

object SampleActivityCatalog {
    val forYou: List<ActivityChallenge> = listOf(
        ActivityChallenge(
            id = "walk-5",
            title = "Five-minute walk",
            description = "Step away from the feed. Walk anywhere nearby and notice three real-world details.",
            durationMinutes = 5,
            category = WellnessCategory.Movement,
            difficulty = 1,
            icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
            isPersonalized = true,
        ),
        ActivityChallenge(
            id = "box-breath",
            title = "Box breathing reset",
            description = "Inhale 4 · hold 4 · exhale 4 · hold 4. Complete three calm cycles before returning.",
            durationMinutes = 2,
            category = WellnessCategory.Mindfulness,
            difficulty = 1,
            icon = Icons.Outlined.SelfImprovement,
            isPersonalized = true,
        ),
        ActivityChallenge(
            id = "phone-down",
            title = "Phone-down stretch",
            description = "Place your phone face-down. Stretch shoulders, neck, and wrists for one full minute.",
            durationMinutes = 1,
            category = WellnessCategory.DigitalDetox,
            difficulty = 1,
            icon = Icons.Outlined.PhoneDisabled,
            isPersonalized = true,
        ),
        ActivityChallenge(
            id = "tea-pause",
            title = "Mindful tea pause",
            description = "Make a warm drink. No screens until the first sip cools enough to taste carefully.",
            durationMinutes = 8,
            category = WellnessCategory.Nourish,
            difficulty = 2,
            icon = Icons.Outlined.LocalCafe,
            isPersonalized = true,
        ),
    )

    val serendipity: List<ActivityChallenge> = listOf(
        ActivityChallenge(
            id = "window-gaze",
            title = "Window gaze",
            description = "Find a window. Soften your eyes and track distant motion for ninety seconds.",
            durationMinutes = 2,
            category = WellnessCategory.Mindfulness,
            difficulty = 1,
            icon = Icons.Outlined.Spa,
            isPersonalized = false,
        ),
        ActivityChallenge(
            id = "stair-sprint",
            title = "Stair micro-sprint",
            description = "Take one flight of stairs twice. Count breaths instead of scrolling notifications.",
            durationMinutes = 3,
            category = WellnessCategory.Movement,
            difficulty = 2,
            icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
            isPersonalized = false,
        ),
        ActivityChallenge(
            id = "text-a-friend",
            title = "Send a real check-in",
            description = "Text one person a specific, kind question — not a meme, not a link.",
            durationMinutes = 3,
            category = WellnessCategory.Social,
            difficulty = 1,
            icon = Icons.Outlined.SelfImprovement,
            isPersonalized = false,
        ),
        ActivityChallenge(
            id = "scroll-free-song",
            title = "One song, eyes closed",
            description = "Play a single track. Eyes closed until it ends. Let the feed wait.",
            durationMinutes = 4,
            category = WellnessCategory.DigitalDetox,
            difficulty = 1,
            icon = Icons.Outlined.PhoneDisabled,
            isPersonalized = false,
        ),
    )

    fun forMode(mode: FeedMode): List<ActivityChallenge> = when (mode) {
        FeedMode.ForYou -> forYou
        FeedMode.Serendipity -> serendipity
    }
}
