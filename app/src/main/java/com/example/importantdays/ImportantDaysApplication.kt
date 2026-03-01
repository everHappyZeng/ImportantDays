package com.example.importantdays

import android.app.Application
import com.example.importantdays.di.AppContainer
import com.example.importantdays.util.NotificationHelper
import com.example.importantdays.util.ReminderChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ImportantDaysApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannel(this)
        ReminderChecker.start(this, container.repository, applicationScope)
    }
}
