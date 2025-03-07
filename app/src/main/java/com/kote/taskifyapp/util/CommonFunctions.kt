package com.kote.taskifyapp.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar

fun calculateReminderTime(dateInMillis: Long, timeInMinutes: Int): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = dateInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val timeInMillis = timeInMinutes * 60 * 1000L
    return calendar.timeInMillis + timeInMillis
}

fun convertLocalDateToMillis(date: LocalDate): Long {
    return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun convertMillisToLocalDate(dayMillis: Long): LocalDate {
    return Instant.ofEpochMilli(dayMillis).atZone(ZoneId.systemDefault()).toLocalDate()
}