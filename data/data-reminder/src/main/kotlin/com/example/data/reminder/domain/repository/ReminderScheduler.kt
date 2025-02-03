package com.example.data.reminder.domain.repository

import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.Source

interface ReminderScheduler {

    fun scheduleReminder(reminderId: Long, reminderTime: Long)

    fun cancelReminder(reminderId: Long)

    suspend fun getReminder(deckId: String, source: Source): Reminder?

    suspend fun getRemindersForDeck(deckId: String, source: Source): List<Reminder>

    suspend fun insertReminder(reminder: Reminder): Long

    suspend fun deleteReminder(deckId: String, source: Source, reminderTime: Long)

    suspend fun getTrainingPlan(startDate: Int, finishDate: Int, preferredTime: Int): List<Long>
}
