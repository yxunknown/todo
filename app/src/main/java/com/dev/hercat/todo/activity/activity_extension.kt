package com.dev.hercat.todo.activity

import android.app.Activity
import com.dev.hercat.todo.database.Database
import java.text.SimpleDateFormat
import java.util.*

val Activity.db
    get() = Database(applicationContext)

fun Activity.formateDate(date: Date): String {
    return with(SimpleDateFormat("dd/MM yy HH:mm", Locale.CHINA)) {
        format(date)
    }
}

fun Activity.getDate(date: String): Date {
    return with(SimpleDateFormat("dd/MM yy HH:mm", Locale.CHINA)) {
        parse(date)
    }
}