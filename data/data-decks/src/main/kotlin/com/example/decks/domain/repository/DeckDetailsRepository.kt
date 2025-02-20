package com.example.decks.domain.repository

import com.example.database.models.Source

interface DeckDetailsRepository {

    suspend fun getNextTrainingTime(deckId: String, source: Source): Long?
}