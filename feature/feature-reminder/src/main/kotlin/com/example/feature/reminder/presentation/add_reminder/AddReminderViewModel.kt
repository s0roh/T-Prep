package com.example.feature.reminder.presentation.add_reminder

import androidx.lifecycle.ViewModel
import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.Source
import com.example.feature.reminder.domain.GetRemindersForDeckUseCase
import com.example.feature.reminder.domain.InsertReminderUseCase
import com.example.feature.reminder.domain.ScheduleReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AddReminderViewModel @Inject constructor(
    private val getRemindersForDeckUseCase: GetRemindersForDeckUseCase,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val insertReminderUseCase: InsertReminderUseCase,

    ) : ViewModel() {

    fun scheduleReminder(reminderId: Long, timeMillis: Long) {
        scheduleReminderUseCase(reminderId, timeMillis)
    }


    suspend fun insertReminder(reminder: Reminder): Long {
        return insertReminderUseCase(reminder)
    }

    suspend fun createReminderIfValid(
        deckId: String,
        source: Source,
        reminderTime: Long,
        deckName: String,
    ): Reminder? {
        val existingReminders = getRemindersForDeck(deckId = deckId, source = source)

        // Проверяем, если уже существует напоминание с таким временем
        if (existingReminders.any { it.reminderTime == reminderTime }) {
            return null
        }

        // Создаем новое напоминание, так как такого времени ещё нет
        return Reminder(
            id = 0,
            reminderTime = reminderTime,
            source = source,
            deckId = deckId,
            name = deckName
        )
    }

    private suspend fun getRemindersForDeck(deckId: String, source: Source): List<Reminder> {
        return getRemindersForDeckUseCase(deckId = deckId, source = source)
    }
}