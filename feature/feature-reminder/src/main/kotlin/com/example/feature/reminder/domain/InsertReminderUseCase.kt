package com.example.feature.reminder.domain

import com.example.data.reminder.domain.entity.Reminder
import com.example.data.reminder.domain.repository.ReminderScheduler
import javax.inject.Inject

class InsertReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {

    suspend operator fun invoke(reminder: Reminder) =
        reminderScheduler.insertReminder(reminder)
}