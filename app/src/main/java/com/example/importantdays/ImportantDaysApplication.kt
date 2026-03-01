package com.example.importantdays

import android.app.Application
import com.example.importantdays.di.AppContainer

class ImportantDaysApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
