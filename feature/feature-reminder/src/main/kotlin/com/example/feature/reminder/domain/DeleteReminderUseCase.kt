package com.example.feature.reminder.domain

import com.example.database.models.Source
import com.example.data.reminder.domain.repository.ReminderScheduler
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {

    suspend operator fun invoke(deckId: Long, source: Source) =
        reminderScheduler.deleteReminder(deckId = deckId, source = source)
}