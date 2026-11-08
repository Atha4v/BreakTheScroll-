package com.breakthescroll.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val darkTheme: Boolean = true,
    val displayName: String = "Atharv",
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletionEpochDay: Long? = null,
    val screenTimeSavedMinutes: Int = 0,
    val tasksCompleted: Int = 0,
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val cadence: String,
    val streakDays: Int = 0,
    val completedToday: Boolean = false,
    val lastCompletedEpochDay: Long? = null,
    val accentArgb: Int,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "activity_completions")
data class ActivityCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityId: String,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "activity_catalog")
data class ActivityCatalogEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val category: String,
    val difficulty: Int,
    val illustration: String,
    val isPersonalized: Boolean,
    val heroColorStart: Int,
    val heroColorEnd: Int,
)
