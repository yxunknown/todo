package com.dev.hercat.todo.activity

import android.app.Activity
import com.dev.hercat.todo.database.Database

val Activity.db
    get() = Database(applicationContext)