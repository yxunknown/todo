package com.dev.hercat.todo.application

import android.app.Application
import com.dev.hercat.todo.activity.updateLanguage

class TaskApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        updateLanguage()
    }
}