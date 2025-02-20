package com.example.decks.data.repository

import com.example.database.TPrepDatabase
import com.example.database.models.Source
import com.example.decks.domain.repository.DeckDetailsRepository
import javax.inject.Inject

class DeckDetailsRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase
): DeckDetailsRepository{


    override suspend fun getNextTrainingTime(deckId: String, source: Source): Long? {
        return database.trainingReminderDao.getNextReminder(deckId, source)?.reminderTime
    }
}