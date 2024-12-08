package com.example.feature.reminder.domain

import com.example.data.reminder.domain.repository.ReminderScheduler
import javax.inject.Inject

class CancelReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {

    operator fun invoke(reminderId: Long) =
        reminderScheduler.cancelReminder(reminderId = reminderId)
}