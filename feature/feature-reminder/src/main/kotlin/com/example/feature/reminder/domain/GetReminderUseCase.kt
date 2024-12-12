package com.example.feature.reminder.domain

import com.example.database.models.Source
import com.example.data.reminder.domain.entity.Reminder
import com.example.data.reminder.domain.repository.ReminderScheduler
import javax.inject.Inject

class GetReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {

    suspend operator fun invoke(deckId: String, source: Source): Reminder? =
        reminderScheduler.getReminder(deckId = deckId, source = source)
}