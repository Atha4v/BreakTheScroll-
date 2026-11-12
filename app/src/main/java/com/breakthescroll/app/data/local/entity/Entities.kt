package com.breakthescroll.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val darkTheme: Boolean = true,
    val displayName: String = "Atharv",
    /** Unique handle used when joining / inviting to groups */
    val username: String = "atharv",
    val interestsCsv: String = "",
    val onboardingComplete: Boolean = false,
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletionEpochDay: Long? = null,
    val screenTimeSavedMinutes: Int = 0,
    val tasksCompleted: Int = 0,
    /** Completions from the 1-tap Instant Scroll Breaker (START). */
    val quickRescues: Int = 0,
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

@Entity(tableName = "habit_completions")
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val habitTitle: String,
    val accentArgb: Int,
    val completedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "activity_completions")
data class ActivityCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityId: String,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val imageUrl: String = "",
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
    val interestTag: String,
    val imageUrl: String,
    val isPersonalized: Boolean,
    val heroColorStart: Int,
    val heroColorEnd: Int,
    /** Live Active Break countdown length for this activity (e.g. 30 or 300). */
    val durationSeconds: Int = 60,
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val message: String,
    val activityTitle: String,
    val category: String,
    val imageUrl: String = "",
    val cheers: Int = 0,
    /** Device/user already liked — prevents duplicate inflation */
    val likedByMe: Boolean = false,
    val isMine: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "squad_groups",
    indices = [Index(value = ["name"])],
)
data class SquadGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val isPublic: Boolean = true,
    val ownerUsername: String,
    val memberCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "group_members",
    indices = [Index(value = ["groupId", "username"], unique = true)],
)
data class GroupMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val username: String,
    val joinedAt: Long = System.currentTimeMillis(),
)
