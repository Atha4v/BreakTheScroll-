package com.breakthescroll.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.breakthescroll.app.data.local.dao.ActivityCatalogDao
import com.breakthescroll.app.data.local.dao.ActivityCompletionDao
import com.breakthescroll.app.data.local.dao.HabitDao
import com.breakthescroll.app.data.local.dao.UserPreferenceDao
import com.breakthescroll.app.data.local.dao.UserStatsDao
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.UserPreferenceEntity
import com.breakthescroll.app.data.local.entity.UserStatsEntity

@Database(
    entities = [
        UserPreferenceEntity::class,
        UserStatsEntity::class,
        HabitEntity::class,
        ActivityCompletionEntity::class,
        ActivityCatalogEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class BreakTheScrollDatabase : RoomDatabase() {
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun habitDao(): HabitDao
    abstract fun activityCompletionDao(): ActivityCompletionDao
    abstract fun activityCatalogDao(): ActivityCatalogDao

    companion object {
        @Volatile
        private var instance: BreakTheScrollDatabase? = null

        fun get(context: Context): BreakTheScrollDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BreakTheScrollDatabase::class.java,
                    "break_the_scroll.db",
                ).build().also { instance = it }
            }
        }
    }
}
