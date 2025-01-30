package com.example.feature.reminder.domain

import com.example.data.reminder.domain.entity.Reminder
import com.example.data.reminder.domain.repository.ReminderScheduler
import com.example.database.models.Source
import javax.inject.Inject

class GetRemindersForDeckUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler,
) {

    suspend operator fun invoke(deckId: String, source: Source): List<Reminder> =
        reminderScheduler.getRemindersForDeck(deckId = deckId, source = source)
}