package com.example.feature.reminder.domain

import com.example.data.reminder.domain.repository.ReminderScheduler
import javax.inject.Inject

internal class ScheduleReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {

    operator fun invoke(reminderId: Long, timeMillis: Long) =
        reminderScheduler.scheduleReminder(reminderId = reminderId, reminderTime = timeMillis)
}