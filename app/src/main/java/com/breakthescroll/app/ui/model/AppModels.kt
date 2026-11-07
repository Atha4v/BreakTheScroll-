package com.breakthescroll.app.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.PhoneDisabled
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.WarmAmber

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

enum class BuddyMood {
    Idle,
    Cheer,
    Rest,
    Nudge,
}

data class ActivityChallenge(
    val id: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val category: WellnessCategory,
    val difficulty: Int = 1,
    val icon: ImageVector,
    val illustration: ActivityIllustration,
    val heroColors: List<Color>,
    val isPersonalized: Boolean = true,
)

data class HabitItem(
    val id: String,
    val title: String,
    val cadence: String,
    val streakDays: Int,
    val completedToday: Boolean,
    val accent: Color,
)

data class SquadGroup(
    val id: String,
    val name: String,
    val members: Int,
    val challenge: String,
    val progress: Float,
)

data class Achievement(
    val id: String,
    val title: String,
    val subtitle: String,
    val unlocked: Boolean,
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
            illustration = ActivityIllustration.Walk,
            heroColors = listOf(Color(0xFF064E3B), Color(0xFF0EA5E9)),
        ),
        ActivityChallenge(
            id = "bike-spin",
            title = "Neighborhood bicycle loop",
            description = "Hop on a cycle for one short loop. Feel wind instead of infinite scroll.",
            durationMinutes = 12,
            category = WellnessCategory.Movement,
            difficulty = 2,
            icon = Icons.AutoMirrored.Outlined.DirectionsBike,
            illustration = ActivityIllustration.Bike,
            heroColors = listOf(Color(0xFF14532D), Color(0xFF10B981)),
        ),
        ActivityChallenge(
            id = "yoga-flow",
            title = "Desk yoga stretch",
            description = "Three gentle poses for neck, hips, and spine. Soften the scroll tension.",
            durationMinutes = 6,
            category = WellnessCategory.Mindfulness,
            difficulty = 1,
            icon = Icons.Outlined.SelfImprovement,
            illustration = ActivityIllustration.Yoga,
            heroColors = listOf(Color(0xFF4C1D95), Color(0xFFA78BFA)),
        ),
        ActivityChallenge(
            id = "box-breath",
            title = "Box breathing reset",
            description = "Inhale 4 · hold 4 · exhale 4 · hold 4. Complete three calm cycles before returning.",
            durationMinutes = 2,
            category = WellnessCategory.Mindfulness,
            difficulty = 1,
            icon = Icons.Outlined.SelfImprovement,
            illustration = ActivityIllustration.Breath,
            heroColors = listOf(Color(0xFF0F766E), Color(0xFF5EEAD4)),
        ),
        ActivityChallenge(
            id = "hydrate",
            title = "Hydration pause",
            description = "Fill a glass, sip slowly, and finish it before unlocking any social app.",
            durationMinutes = 2,
            category = WellnessCategory.Nourish,
            difficulty = 1,
            icon = Icons.Outlined.WaterDrop,
            illustration = ActivityIllustration.Hydrate,
            heroColors = listOf(Color(0xFF0C4A6E), Color(0xFF38BDF8)),
        ),
        ActivityChallenge(
            id = "tea-pause",
            title = "Mindful tea pause",
            description = "Make a warm drink. No screens until the first sip cools enough to taste carefully.",
            durationMinutes = 8,
            category = WellnessCategory.Nourish,
            difficulty = 2,
            icon = Icons.Outlined.LocalCafe,
            illustration = ActivityIllustration.Tea,
            heroColors = listOf(Color(0xFF78350F), Color(0xFFF59E0B)),
        ),
    )

    val serendipity: List<ActivityChallenge> = listOf(
        ActivityChallenge(
            id = "read-page",
            title = "One page offline",
            description = "Open a physical book or magazine. Read one page with full attention.",
            durationMinutes = 5,
            category = WellnessCategory.Mindfulness,
            difficulty = 1,
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            illustration = ActivityIllustration.Read,
            heroColors = listOf(Color(0xFF7C2D12), Color(0xFFFB923C)),
            isPersonalized = false,
        ),
        ActivityChallenge(
            id = "window-gaze",
            title = "Window gaze",
            description = "Find a window. Soften your eyes and track distant motion for ninety seconds.",
            durationMinutes = 2,
            category = WellnessCategory.Mindfulness,
            difficulty = 1,
            icon = Icons.Outlined.Spa,
            illustration = ActivityIllustration.Window,
            heroColors = listOf(Color(0xFF1E3A8A), Color(0xFF93C5FD)),
            isPersonalized = false,
        ),
        ActivityChallenge(
            id = "phone-down",
            title = "Phone-down stretch",
            description = "Place your phone face-down. Stretch shoulders, neck, and wrists for one full minute.",
            durationMinutes = 1,
            category = WellnessCategory.DigitalDetox,
            difficulty = 1,
            icon = Icons.Outlined.PhoneDisabled,
            illustration = ActivityIllustration.PhoneDown,
            heroColors = listOf(Color(0xFF3F1D2E), Color(0xFFF472B6)),
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
            illustration = ActivityIllustration.Social,
            heroColors = listOf(Color(0xFF831843), Color(0xFFF9A8D4)),
            isPersonalized = false,
        ),
    )

    fun forMode(mode: FeedMode): List<ActivityChallenge> = when (mode) {
        FeedMode.ForYou -> forYou
        FeedMode.Serendipity -> serendipity
    }
}

object SampleHabits {
    val items = listOf(
        HabitItem("h1", "Morning stretch", "Daily · 7:30 AM", 12, true, SereneMint),
        HabitItem("h2", "No-phone breakfast", "Weekdays", 5, false, WarmAmber),
        HabitItem("h3", "Evening walk", "Daily · 7 PM", 21, true, Color(0xFF38BDF8)),
        HabitItem("h4", "Gratitude note", "Nightly", 8, false, Color(0xFFA78BFA)),
    )
}

object SampleProgress {
    val hoursSavedThisWeek = 6.4f
    val tasksCompleted = 18
    val currentStreak = 7
    val weeklyBars = listOf(0.4f, 0.7f, 0.55f, 0.9f, 0.65f, 0.8f, 0.5f)
    val heatmap = List(35) { index ->
        when {
            index % 7 == 0 -> 0.2f
            index % 3 == 0 -> 0.85f
            index % 2 == 0 -> 0.55f
            else -> 0.35f
        }
    }
}

object SampleProfile {
    val displayName = "Atharv"
    val companionLevel = 4
    val achievements = listOf(
        Achievement("a1", "Scroll Breaker", "Accepted 10 micro-tasks", true),
        Achievement("a2", "Breath Keeper", "Finished 5 resets", true),
        Achievement("a3", "Squad Spark", "Joined a group challenge", false),
        Achievement("a4", "Streak Fox", "7-day streak with Buddy", true),
    )
    val squads = listOf(
        SquadGroup("g1", "Dawn Movers", 14, "10k collective steps", 0.72f),
        SquadGroup("g2", "Focus Pod", 8, "No doomscroll evenings", 0.48f),
        SquadGroup("g3", "Hydration Crew", 22, "8 glasses daily", 0.91f),
    )
}
