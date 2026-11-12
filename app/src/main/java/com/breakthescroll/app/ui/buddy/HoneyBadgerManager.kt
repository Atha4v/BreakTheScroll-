package com.breakthescroll.app.ui.buddy

import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.BuddyScreenContext
import kotlin.random.Random

/**
 * Rotating ScrollBuddy quote engine — never sticks to a single hardcoded line.
 */
object HoneyBadgerManager {
    private val launch = listOf(
        "The algorithm isn't your friend, but I am. Let's do this.",
        "Feeds sell urgency. We sell one quiet win.",
        "Chalo, ek chhoti si real-world win pakadte hain.",
        "Doomscroll later. Persistence pehle.",
    )
    private val timerStart = listOf(
        "Eyes on the ring. Phone can sulk without you.",
        "Timer on. Cheap swipes off. Badger rules.",
        "Breathe with me — feed wait kar sakta hai.",
        "This is the part where you actually show up.",
        "Pattern interrupt engaged. Scroll loop broken — stay with it.",
        "Emergency reset. Badger's proud already. Finish strong.",
    )
    private val taskDone = listOf(
        "That's persistence, chief. Scrolling couldn't have done that 🦡",
        "Quiet flex logged. Timeline jealous ho raha hoga.",
        "Mast. One real win > thirty empty refreshes.",
        "Never give up energy. Receipt saved.",
    )
    private val streakQuotes = listOf(
        "🔥 Streak heat. The feed misses you — keep starving it.",
        "Day chain holding. Algorithm se zyada loyal ho tum.",
        "Embers lit. Don't let a reel extinguish this.",
        "Milestone vibes. Badger doesn't break streaks for reels.",
    )
    private val nudge = listOf(
        "Scrolling won't fix your code. Take two minutes and move.",
        "Pass is fine. Quitting the feed mid-swipe is finer.",
        "Feed chor, real world try kar. Badger doesn't quit.",
        "That was almost a win. Almost doesn't fill Progress.",
    )
    private val habits = listOf(
        "One checkbox beats thirty minutes of 'just checking'.",
        "Habit logged. Quiet flex > public reel.",
        "Routine > motivation. Mark it and walk away.",
        "Chhote reps, badi chain. Keep going.",
    )
    private val groups = listOf(
        "Squad's watching. Post a win, not a doomscroll diary.",
        "Cheer someone. Momentum is contagious.",
        "Real groups > fake urgency. Invite with @username.",
        "Pack hunt > solo scroll. Join or create.",
    )
    private val progress = listOf(
        "Your Room history is the only timeline that matters.",
        "Receipts don't lie. Neither do I.",
        "Empty log is tragic. Fill it with timers, not tabs.",
        "Hours saved = hours the feed didn't steal.",
    )

    fun quote(
        context: BuddyScreenContext,
        mood: BuddyMood,
        streak: Int,
        tasksCompleted: Int,
    ): String {
        val pool = when {
            mood == BuddyMood.Working -> timerStart
            mood == BuddyMood.Cheer && streak >= 3 -> streakQuotes
            mood == BuddyMood.Cheer -> taskDone
            mood == BuddyMood.Nudge -> nudge
            context == BuddyScreenContext.Habits -> habits
            context == BuddyScreenContext.Groups -> groups
            context == BuddyScreenContext.Progress -> progress
            tasksCompleted == 0 -> launch
            else -> launch + progress
        }
        return pool.random()
    }

    fun quoteForTimer(): String = timerStart.random()

    fun quoteForHub(): String = listOf(
        "Pick a reset. Three paths, zero doomscroll.",
        "Hub open. Choose calm, focus, or streak shield.",
        "START isn't a gimmick — it's an interception.",
    ).random()
}
