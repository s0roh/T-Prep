package com.example.data.reminder.domain.repository

import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.Source

interface ReminderScheduler {

    fun scheduleReminder(reminderId: Long, timeMillis: Long)

    fun cancelReminder(reminderId: Long)

    suspend fun getReminder(deckId: String, source: Source): Reminder?

    suspend fun insertReminder(reminder: Reminder): Long

    suspend fun deleteReminder(deckId: String, source: Source)
}
