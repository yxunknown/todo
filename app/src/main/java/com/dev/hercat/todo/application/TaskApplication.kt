package com.dev.hercat.todo.application

import android.app.Application
import com.dev.hercat.todo.activity.updateLanguage
import com.dev.hercat.todo.service.NotificationService
import org.jetbrains.anko.intentFor

class TaskApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        updateLanguage()

        //start notification service
        startService(intentFor<NotificationService>())
    }
}