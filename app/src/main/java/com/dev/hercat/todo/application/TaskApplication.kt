package com.dev.hercat.todo.application

import android.app.Application
import android.content.Context
import com.dev.hercat.todo.activity.updateLanguage
import com.dev.hercat.todo.service.NotificationService
import org.jetbrains.anko.intentFor

class TaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        updateLanguage()

        //start notification service
        val sp = getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE)
        val shouldNotification = sp.getBoolean("should_notification", true)
        if (shouldNotification) {
            startService(intentFor<NotificationService>())
        }
    }
}