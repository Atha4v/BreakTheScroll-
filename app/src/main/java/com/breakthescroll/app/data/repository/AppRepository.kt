package com.breakthescroll.app.data.repository

import com.breakthescroll.app.data.local.BreakTheScrollDatabase
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.UserPreferenceEntity
import com.breakthescroll.app.data.local.entity.UserStatsEntity
import com.breakthescroll.app.data.seed.ActivityCatalogSeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max

class AppRepository(private val db: BreakTheScrollDatabase) {
    private val prefs = db.userPreferenceDao()
    private val statsDao = db.userStatsDao()
    private val habitsDao = db.habitDao()
    private val completionsDao = db.activityCompletionDao()
    private val catalogDao = db.activityCatalogDao()

    suspend fun bootstrap() {
        if (prefs.get() == null) {
            prefs.upsert(UserPreferenceEntity(darkTheme = true))
        }
        if (statsDao.get() == null) {
            statsDao.upsert(UserStatsEntity())
        }
        if (catalogDao.count() == 0) {
            catalogDao.upsertAll(ActivityCatalogSeed.items)
        }
    }

    fun observeDarkTheme(): Flow<Boolean> =
        prefs.observe().map { it?.darkTheme ?: true }

    fun observeDisplayName(): Flow<String> =
        prefs.observe().map { it?.displayName ?: "Atharv" }

    suspend fun setDarkTheme(darkTheme: Boolean) {
        val current = prefs.get() ?: UserPreferenceEntity()
        prefs.upsert(current.copy(darkTheme = darkTheme))
    }

    fun observeStats(): Flow<UserStatsEntity> =
        statsDao.observe().map { it ?: UserStatsEntity() }

    fun observeHabits(): Flow<List<HabitEntity>> =
        habitsDao.observeAll().map { list ->
            val today = LocalDate.now().toEpochDay()
            list.map { habit ->
                if (habit.completedToday && habit.lastCompletedEpochDay != today) {
                    habit.copy(completedToday = false)
                } else {
                    habit
                }
            }
        }

    fun observeForYou(): Flow<List<ActivityCatalogEntity>> = catalogDao.observeForYou()

    fun observeSerendipity(): Flow<List<ActivityCatalogEntity>> = catalogDao.observeSerendipity()

    fun observeRecentCompletions(limit: Int = 20): Flow<List<ActivityCompletionEntity>> =
        completionsDao.observeRecent(limit)

    fun observeAllCompletions(): Flow<List<ActivityCompletionEntity>> =
        completionsDao.observeAll()

    suspend fun addHabit(title: String, cadence: String, accentArgb: Int) {
        habitsDao.insert(
            HabitEntity(
                title = title.trim().ifBlank { "New micro-habit" },
                cadence = cadence.trim().ifBlank { "Daily" },
                accentArgb = accentArgb,
            ),
        )
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        val today = LocalDate.now().toEpochDay()
        val updated = if (!habit.completedToday || habit.lastCompletedEpochDay != today) {
            habit.copy(
                completedToday = true,
                streakDays = habit.streakDays + 1,
                lastCompletedEpochDay = today,
            )
        } else {
            habit.copy(
                completedToday = false,
                streakDays = max(0, habit.streakDays - 1),
            )
        }
        habitsDao.update(updated)
    }

    suspend fun deleteHabit(id: Long) {
        habitsDao.delete(id)
    }

    suspend fun acceptActivity(item: ActivityCatalogEntity) {
        completionsDao.insert(
            ActivityCompletionEntity(
                activityId = item.id,
                title = item.title,
                category = item.category,
                durationMinutes = item.durationMinutes,
            ),
        )
        bumpStats(item.durationMinutes)
    }

    suspend fun completeBreathingReset(minutesSaved: Int = 2) {
        completionsDao.insert(
            ActivityCompletionEntity(
                activityId = "breath-reset",
                title = "Instant breathing reset",
                category = "Mindfulness",
                durationMinutes = minutesSaved,
            ),
        )
        bumpStats(minutesSaved)
    }

    private suspend fun bumpStats(minutes: Int) {
        val current = statsDao.get() ?: UserStatsEntity()
        val today = LocalDate.now().toEpochDay()
        val yesterday = today - 1
        val newStreak = when (current.lastCompletionEpochDay) {
            null -> 1
            today -> current.currentStreak.coerceAtLeast(1)
            yesterday -> current.currentStreak + 1
            else -> 1
        }
        statsDao.upsert(
            current.copy(
                currentStreak = newStreak,
                longestStreak = max(current.longestStreak, newStreak),
                lastCompletionEpochDay = today,
                screenTimeSavedMinutes = current.screenTimeSavedMinutes + minutes,
                tasksCompleted = current.tasksCompleted + 1,
            ),
        )
    }

    suspend fun weeklyBarFractions(): List<Float> {
        val zone = ZoneId.systemDefault()
        val monday = LocalDate.now().with(DayOfWeek.MONDAY)
        return (0..6).map { offset ->
            val day = monday.plusDays(offset.toLong())
            val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val minutes = completionsDao.minutesBetween(start, end)
            if (minutes <= 0) 0.08f else (minutes / 40f).coerceIn(0.15f, 1f)
        }
    }

    suspend fun heatmapIntensities(days: Int = 35): List<Float> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        return (days - 1 downTo 0).map { back ->
            val day = today.minusDays(back.toLong())
            val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            when (val count = completionsDao.countBetween(start, end)) {
                0 -> 0.12f
                1 -> 0.35f
                2 -> 0.55f
                3 -> 0.75f
                else -> 0.95f
            }
        }
    }
}
