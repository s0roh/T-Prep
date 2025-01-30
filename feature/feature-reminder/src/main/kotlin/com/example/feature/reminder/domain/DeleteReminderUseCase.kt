package com.example.feature.reminder.domain

import com.example.database.models.Source
import com.example.data.reminder.domain.repository.ReminderScheduler
import javax.inject.Inject

internal class DeleteReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler,
) {

    suspend operator fun invoke(deckId: String, source: Source, reminderTime: Long) =
        reminderScheduler.deleteReminder(deckId = deckId, source = source, reminderTime = reminderTime)
}