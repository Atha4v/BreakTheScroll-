package com.breakthescroll.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.CommunityPostEntity
import com.breakthescroll.app.data.local.entity.GroupMemberEntity
import com.breakthescroll.app.data.local.entity.HabitCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.SquadGroupEntity
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
interface HabitCompletionDao {
    @Query("SELECT * FROM habit_completions ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<HabitCompletionEntity>>

    @Query(
        """
        SELECT * FROM habit_completions
        WHERE completedAt >= :startInclusive AND completedAt < :endExclusive
        ORDER BY completedAt DESC
        """,
    )
    fun observeBetween(startInclusive: Long, endExclusive: Long): Flow<List<HabitCompletionEntity>>

    @Insert
    suspend fun insert(completion: HabitCompletionEntity): Long

    @Query(
        """
        DELETE FROM habit_completions
        WHERE habitId = :habitId AND completedAt >= :dayStart AND completedAt < :dayEnd
        """,
    )
    suspend fun deleteForHabitOnDay(habitId: Long, dayStart: Long, dayEnd: Long)
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
    @Query("SELECT * FROM activity_catalog")
    fun observeAll(): Flow<List<ActivityCatalogEntity>>

    @Query("SELECT * FROM activity_catalog WHERE isPersonalized = 1")
    fun observeForYou(): Flow<List<ActivityCatalogEntity>>

    @Query("SELECT * FROM activity_catalog WHERE isPersonalized = 0")
    fun observeSerendipity(): Flow<List<ActivityCatalogEntity>>

    @Query("SELECT COUNT(*) FROM activity_catalog")
    suspend fun count(): Int

    @Query("DELETE FROM activity_catalog")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ActivityCatalogEntity>)
}

@Dao
interface CommunityPostDao {
    @Query("SELECT * FROM community_posts ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CommunityPostEntity>>

    @Insert
    suspend fun insert(post: CommunityPostEntity): Long

    @Query("SELECT * FROM community_posts WHERE id = :id")
    suspend fun get(id: Long): CommunityPostEntity?

    @Update
    suspend fun update(post: CommunityPostEntity)

    @Query("SELECT COUNT(*) FROM community_posts")
    suspend fun count(): Int
}

@Dao
interface SquadGroupDao {
    @Query("SELECT * FROM squad_groups ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SquadGroupEntity>>

    @Query(
        """
        SELECT * FROM squad_groups
        WHERE isPublic = 1 AND (
            name LIKE '%' || :query || '%' OR
            category LIKE '%' || :query || '%' OR
            description LIKE '%' || :query || '%'
        )
        ORDER BY memberCount DESC, createdAt DESC
        """,
    )
    fun searchPublic(query: String): Flow<List<SquadGroupEntity>>

    @Query("SELECT * FROM squad_groups WHERE isPublic = 1 ORDER BY memberCount DESC, createdAt DESC")
    fun observePublic(): Flow<List<SquadGroupEntity>>

    @Query(
        """
        SELECT g.* FROM squad_groups g
        INNER JOIN group_members m ON m.groupId = g.id
        WHERE lower(m.username) = lower(:username)
        ORDER BY g.createdAt DESC
        """,
    )
    fun observeJoinedByUsername(username: String): Flow<List<SquadGroupEntity>>

    @Insert
    suspend fun insert(group: SquadGroupEntity): Long

    @Update
    suspend fun update(group: SquadGroupEntity)

    @Query("SELECT * FROM squad_groups WHERE id = :id")
    suspend fun get(id: Long): SquadGroupEntity?

    @Query("SELECT COUNT(*) FROM squad_groups")
    suspend fun count(): Int
}

@Dao
interface GroupMemberDao {
    @Query("SELECT * FROM group_members WHERE groupId = :groupId ORDER BY joinedAt ASC")
    fun observeForGroup(groupId: Long): Flow<List<GroupMemberEntity>>

    @Query("SELECT * FROM group_members WHERE groupId = :groupId ORDER BY joinedAt ASC")
    suspend fun listForGroup(groupId: Long): List<GroupMemberEntity>

    @Query(
        """
        SELECT COUNT(*) FROM group_members
        WHERE groupId = :groupId AND lower(username) = lower(:username)
        """,
    )
    suspend fun isMember(groupId: Long, username: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(member: GroupMemberEntity): Long

    @Query("DELETE FROM group_members WHERE groupId = :groupId AND lower(username) = lower(:username)")
    suspend fun remove(groupId: Long, username: String)
}
