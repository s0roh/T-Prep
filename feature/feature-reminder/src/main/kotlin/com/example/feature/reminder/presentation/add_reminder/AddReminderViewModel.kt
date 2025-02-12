package com.example.feature.reminder.presentation.add_reminder

import androidx.lifecycle.ViewModel
import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.Source
import com.example.feature.reminder.domain.GetRemindersForDeckUseCase
import com.example.feature.reminder.domain.GetTrainingPlanUseCase
import com.example.feature.reminder.domain.InsertReminderUseCase
import com.example.feature.reminder.domain.ScheduleReminderUseCase
import com.example.feature.reminder.presentation.util.calculateAdjustedDates
import com.example.feature.reminder.presentation.util.combineDateAndTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AddReminderViewModel @Inject constructor(
    private val getRemindersForDeckUseCase: GetRemindersForDeckUseCase,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val insertReminderUseCase: InsertReminderUseCase,
    private val getTrainingPlanUseCase: GetTrainingPlanUseCase,

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

    suspend fun loadTrainingPlan(startDate: Int, finishDate: Int, preferredTime: Int): List<Long> =
        getTrainingPlanUseCase(
            startDate = startDate,
            finishDate = finishDate,
            preferredTime = preferredTime
        )

    private suspend fun getRemindersForDeck(deckId: String, source: Source): List<Reminder> {
        return getRemindersForDeckUseCase(deckId = deckId, source = source)
    }

    suspend fun createAutoReminders(
        startDate: Long?, endDate: Long?, preferredTime: Long?,
        deckId: String, deckName: String, source: Source
    ): String? {
        if (startDate == null || endDate == null || preferredTime == null) {
            return "Выберите дату начала, окончания и предпочитаемое время"
        }

        if (endDate < startDate) {
            return "Дата окончания должна быть позже даты начала"
        }

        val (adjustedStartDate, adjustedEndDate, adjustedPreferredTime) =
            calculateAdjustedDates(startDate, endDate, preferredTime)

        if (adjustedEndDate - adjustedStartDate < MILLISECONDS_IN_DAY) {
            return "Разница между датой начала и окончания должна быть не менее суток"
        }

        if (adjustedStartDate < System.currentTimeMillis()) {
            return "Дата начала не может быть в прошлом"
        }

        val remindersTime = loadTrainingPlan(
            (adjustedStartDate / MILLISECONDS_IN_SECOND).toInt(),
            (adjustedEndDate / MILLISECONDS_IN_SECOND).toInt(),
            adjustedPreferredTime
        )

        remindersTime.forEach { reminderTime ->
            val reminder = createReminderIfValid(deckId, source, reminderTime, deckName)
            if (reminder == null) {
                return "Напоминание на это время уже установлено"
            }
            val reminderId = insertReminder(reminder)
            scheduleReminder(reminderId, reminderTime)
        }

        return null
    }

    suspend fun createManualReminder(
        selectedDate: Long?, selectedTime: Long?,
        deckId: String, deckName: String, source: Source
    ): String? {
        val dateTimeInMillis = combineDateAndTime(selectedDate, selectedTime)
            ?: return "Выберите дату и время"

        val reminder = createReminderIfValid(deckId, source, dateTimeInMillis, deckName)
            ?: return "Напоминание на это время уже установлено"

        val reminderId = insertReminder(reminder)
        scheduleReminder(reminderId, dateTimeInMillis)

        return null
    }

    companion object {

        private const val MILLISECONDS_IN_SECOND = 1000L
        private const val MILLISECONDS_IN_DAY = 24 * 60 * 60 * MILLISECONDS_IN_SECOND
    }
}