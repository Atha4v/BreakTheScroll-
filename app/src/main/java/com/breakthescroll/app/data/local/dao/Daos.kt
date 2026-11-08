package com.breakthescroll.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.UserPreferenceEntity
import com.breakthescroll.app.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun observe(): Flow<UserPreferenceEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun get(): UserPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pref: UserPreferenceEntity)

    @Query("UPDATE user_preferences SET darkTheme = :darkTheme WHERE id = 1")
    suspend fun setDarkTheme(darkTheme: Boolean)
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun observe(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: UserStatsEntity)

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun get(): UserStatsEntity?
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<HabitEntity>>

    @Insert
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ActivityCompletionDao {
    @Query("SELECT * FROM activity_completions ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<ActivityCompletionEntity>>

    @Query("SELECT * FROM activity_completions ORDER BY completedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<ActivityCompletionEntity>>

    @Insert
    suspend fun insert(completion: ActivityCompletionEntity): Long

    @Query(
        """
        SELECT COUNT(*) FROM activity_completions
        WHERE completedAt >= :startInclusive AND completedAt < :endExclusive
        """,
    )
    suspend fun countBetween(startInclusive: Long, endExclusive: Long): Int

    @Query(
        """
        SELECT COALESCE(SUM(durationMinutes), 0) FROM activity_completions
        WHERE completedAt >= :startInclusive AND completedAt < :endExclusive
        """,
    )
    suspend fun minutesBetween(startInclusive: Long, endExclusive: Long): Int
}

@Dao
interface ActivityCatalogDao {
    @Query("SELECT * FROM activity_catalog WHERE isPersonalized = 1")
    fun observeForYou(): Flow<List<ActivityCatalogEntity>>

    @Query("SELECT * FROM activity_catalog WHERE isPersonalized = 0")
    fun observeSerendipity(): Flow<List<ActivityCatalogEntity>>

    @Query("SELECT COUNT(*) FROM activity_catalog")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ActivityCatalogEntity>)
}
