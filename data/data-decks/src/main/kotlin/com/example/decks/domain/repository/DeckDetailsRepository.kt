package com.example.decks.domain.repository

interface DeckDetailsRepository {

    suspend fun getNextTrainingTime(deckId: String): Long?
}