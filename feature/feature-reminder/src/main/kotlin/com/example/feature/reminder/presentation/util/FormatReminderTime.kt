package com.example.feature.reminder.presentation.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
internal fun formatReminderTime(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd.MM.yyyy 'в' HH:mm")
    return formatter.format(date)
}