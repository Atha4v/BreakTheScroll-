package com.breakthescroll.app

import android.app.Application
import android.util.Log
import com.breakthescroll.app.data.local.BreakTheScrollDatabase
import com.breakthescroll.app.data.repository.AppRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BreakTheScrollApplication : Application() {
    private val crashHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Background bootstrap failed", throwable)
    }
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + crashHandler)

    val database: BreakTheScrollDatabase by lazy {
        runCatching { BreakTheScrollDatabase.get(this) }
            .getOrElse { error ->
                Log.e(TAG, "Database open failed — rebuilding", error)
                BreakTheScrollDatabase.reset(this)
            }
    }

    val repository: AppRepository by lazy { AppRepository(database) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            runCatching { repository.bootstrap() }
                .onFailure { Log.e(TAG, "bootstrap error", it) }
        }
    }

    companion object {
        private const val TAG = "BreakTheScroll"
    }
}
