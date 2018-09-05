package com.dev.hercat.todo.activity

import android.app.Activity
import android.content.Context
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
fun Context.updateLanguage() {
    val sp = getSharedPreferences("language", Context.MODE_PRIVATE)
    val index = sp.getInt("language", 0)
    val language =  when(index) {
        0 -> Locale.getDefault()
        1 -> Locale.SIMPLIFIED_CHINESE
        2 -> Locale.TRADITIONAL_CHINESE
        3 -> Locale.TAIWAN
        4 -> Locale.ENGLISH
        else -> Locale.getDefault()
    }
    val configuration = resources.configuration
    configuration.setLocale(language)
    resources.updateConfiguration(configuration, resources.displayMetrics)
    createConfigurationContext(configuration)
}

val Context.db
    get() = Database(applicationContext)