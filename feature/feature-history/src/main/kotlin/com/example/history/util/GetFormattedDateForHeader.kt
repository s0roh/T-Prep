package com.example.history.util

import java.util.Calendar
import java.util.Locale

internal fun getFormattedDateForHeader(timestamp: Long): String {
    val currentDate = Calendar.getInstance()
    val trainingDate = Calendar.getInstance().apply { timeInMillis = timestamp }

    return when {
        currentDate[Calendar.YEAR] == trainingDate[Calendar.YEAR] &&
                currentDate[Calendar.MONTH] == trainingDate[Calendar.MONTH] &&
                currentDate[Calendar.DAY_OF_MONTH] == trainingDate[Calendar.DAY_OF_MONTH] -> "Сегодня"

        currentDate[Calendar.YEAR] == trainingDate[Calendar.YEAR] &&
                currentDate[Calendar.MONTH] == trainingDate[Calendar.MONTH] &&
                currentDate[Calendar.DAY_OF_MONTH] == trainingDate[Calendar.DAY_OF_MONTH] + 1 -> "Вчера"

        else -> {
            val day = trainingDate[Calendar.DAY_OF_MONTH]
            val month =
                trainingDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val year = trainingDate[Calendar.YEAR]
            "$day $month $year"
        }
    }
}