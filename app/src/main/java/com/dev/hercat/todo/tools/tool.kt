package com.dev.hercat.todo.tools

import java.text.SimpleDateFormat
import java.util.*

fun getDate(date: String): Date {
    return with(SimpleDateFormat("dd/MM yyyy HH:mm:ss", Locale.CHINA)) {
        parse(date)
    }
}