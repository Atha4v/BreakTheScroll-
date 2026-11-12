package com.breakthescroll.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.breakthescroll.app.data.local.dao.ActivityCatalogDao
import com.breakthescroll.app.data.local.dao.ActivityCompletionDao
import com.breakthescroll.app.data.local.dao.CommunityPostDao
import com.breakthescroll.app.data.local.dao.GroupMemberDao
import com.breakthescroll.app.data.local.dao.HabitCompletionDao
import com.breakthescroll.app.data.local.dao.HabitDao
import com.breakthescroll.app.data.local.dao.SquadGroupDao
import com.breakthescroll.app.data.local.dao.UserPreferenceDao
import com.breakthescroll.app.data.local.dao.UserStatsDao
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.CommunityPostEntity
import com.breakthescroll.app.data.local.entity.GroupMemberEntity
import com.breakthescroll.app.data.local.entity.HabitCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.SquadGroupEntity
import com.breakthescroll.app.data.local.entity.UserPreferenceEntity
import com.breakthescroll.app.data.local.entity.UserStatsEntity

@Database(
    entities = [
        UserPreferenceEntity::class,
        UserStatsEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        ActivityCompletionEntity::class,
        ActivityCatalogEntity::class,
        CommunityPostEntity::class,
        SquadGroupEntity::class,
        GroupMemberEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class BreakTheScrollDatabase : RoomDatabase() {
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun activityCompletionDao(): ActivityCompletionDao
    abstract fun activityCatalogDao(): ActivityCatalogDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun squadGroupDao(): SquadGroupDao
    abstract fun groupMemberDao(): GroupMemberDao

    companion object {
        @Volatile
        private var instance: BreakTheScrollDatabase? = null

        fun get(context: Context): BreakTheScrollDatabase {
            return instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }
        }

        fun reset(context: Context): BreakTheScrollDatabase {
            synchronized(this) {
                instance?.close()
                instance = null
                context.applicationContext.deleteDatabase("break_the_scroll.db")
                return build(context).also { instance = it }
            }
        }

        private fun build(context: Context): BreakTheScrollDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                BreakTheScrollDatabase::class.java,
                "break_the_scroll.db",
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
