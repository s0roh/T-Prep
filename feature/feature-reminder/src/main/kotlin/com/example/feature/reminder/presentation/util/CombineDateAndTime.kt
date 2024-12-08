package com.example.feature.reminder.presentation.util

import java.util.Calendar

internal fun combineDateAndTime(dateInMillis: Long?, timeInMillis: Long?): Long? {
    if (dateInMillis == null || timeInMillis == null) return null

    val dateCalendar = Calendar.getInstance().apply {
        setTimeInMillis(dateInMillis)
    }
    val timeCalendar = Calendar.getInstance().apply {
        setTimeInMillis(timeInMillis)
    }

    return Calendar.getInstance().apply {
        set(
            dateCalendar.get(Calendar.YEAR),
            dateCalendar.get(Calendar.MONTH),
            dateCalendar.get(Calendar.DAY_OF_MONTH),
            timeCalendar.get(Calendar.HOUR_OF_DAY),
            timeCalendar.get(Calendar.MINUTE)
        )
    }.timeInMillis
}