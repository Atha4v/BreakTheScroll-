package com.breakthescroll.app

import android.app.Application
import com.breakthescroll.app.data.local.BreakTheScrollDatabase
import com.breakthescroll.app.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BreakTheScrollApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: BreakTheScrollDatabase by lazy { BreakTheScrollDatabase.get(this) }
    val repository: AppRepository by lazy { AppRepository(database) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            repository.bootstrap()
        }
    }
}
