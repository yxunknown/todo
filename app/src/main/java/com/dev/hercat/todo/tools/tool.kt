package com.dev.hercat.todo.tools

import java.text.SimpleDateFormat
import java.util.*

fun getDate(date: String): Date {
    return with(SimpleDateFormat("dd/MM yy HH:mm", Locale.CHINA)) {
        parse(date)
    }
}

operator fun Date.minus(other: Date): Long {
    return (this.time - other.time) / (1000 * 60)
}

fun formatDate(): String {
    return with(SimpleDateFormat("dd/MM yy HH:mm", Locale.CHINA)) {
        format(Date())
    }
}