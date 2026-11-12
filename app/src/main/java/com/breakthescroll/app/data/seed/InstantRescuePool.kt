package com.breakthescroll.app.data.seed

import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import kotlin.random.Random

/** 1-tap Instant Scroll Breaker challenges — 45–60s pattern interrupts. */
object InstantRescuePool {
    private val rescues = listOf(
        rescue(
            id = "rescue-eyes",
            title = "20-Second Eye Focus Shift",
            description = "Look far, then near. Break the scroll trance.",
            seconds = 50,
            stepsHint = "eyes",
            start = 0xFF1E3A8A.toInt(),
            end = 0xFF93C5FD.toInt(),
            illustration = "Window",
        ),
        rescue(
            id = "rescue-squats",
            title = "10 Air Squats Right Now",
            description = "Stand up. Ten clean squats. Phone stays down.",
            seconds = 55,
            stepsHint = "squats",
            start = 0xFF7F1D1D.toInt(),
            end = 0xFFF87171.toInt(),
            illustration = "Yoga",
        ),
        rescue(
            id = "rescue-cold",
            title = "Cold Water Splash Reset",
            description = "Splash cold water. Breathe. Interrupt the loop.",
            seconds = 45,
            stepsHint = "cold",
            start = 0xFF0C4A6E.toInt(),
            end = 0xFF38BDF8.toInt(),
            illustration = "Hydrate",
        ),
        rescue(
            id = "rescue-shake",
            title = "Full-Body Shake-Out",
            description = "Shake arms, legs, jaw. Dump the nervous energy.",
            seconds = 50,
            stepsHint = "shake",
            start = 0xFF9A3412.toInt(),
            end = 0xFFFBBF24.toInt(),
            illustration = "Walk",
        ),
        rescue(
            id = "rescue-breath",
            title = "Emergency Box Breath",
            description = "In 4 · hold 4 · out 4 · hold 4. Twice through.",
            seconds = 60,
            stepsHint = "breath",
            start = 0xFF0F766E.toInt(),
            end = 0xFF5EEAD4.toInt(),
            illustration = "Breath",
        ),
        rescue(
            id = "rescue-push",
            title = "Wall Push-Off Burst",
            description = "Fifteen controlled wall presses. Feel the chest open.",
            seconds = 50,
            stepsHint = "push",
            start = 0xFF991B1B.toInt(),
            end = 0xFFFCA5A5.toInt(),
            illustration = "Yoga",
        ),
        rescue(
            id = "rescue-name",
            title = "5-Sense Snap Back",
            description = "Name 1 thing you see, hear, feel, smell, taste.",
            seconds = 55,
            stepsHint = "senses",
            start = 0xFF5B21B6.toInt(),
            end = 0xFFC4B5FD.toInt(),
            illustration = "Breath",
        ),
        rescue(
            id = "rescue-march",
            title = "High-Knee Desk March",
            description = "March in place. Knees up. Scroll impulse down.",
            seconds = 45,
            stepsHint = "march",
            start = 0xFF14532D.toInt(),
            end = 0xFF4ADE80.toInt(),
            illustration = "Walk",
        ),
    )

    fun random(): ActivityCatalogEntity = rescues[Random.nextInt(rescues.size)]

    private fun rescue(
        id: String,
        title: String,
        description: String,
        seconds: Int,
        stepsHint: String,
        start: Int,
        end: Int,
        illustration: String,
    ) = ActivityCatalogEntity(
        id = id,
        title = title,
        description = description,
        durationMinutes = 1,
        category = "Quick Rescue",
        difficulty = 1,
        illustration = illustration,
        interestTag = stepsHint,
        imageUrl = "",
        isPersonalized = false,
        heroColorStart = start,
        heroColorEnd = end,
        durationSeconds = seconds.coerceIn(45, 60),
    )
}
