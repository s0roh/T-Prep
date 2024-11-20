package com.example.decks.domain.repository

import com.example.decks.domain.entity.Deck
import kotlinx.coroutines.flow.SharedFlow

interface DeckRepository {

    fun getPublicDecksFlow(): SharedFlow<List<Deck>>

    suspend fun getDeckById(id: Long): Deck

    suspend fun loadNextPublicDecks()
}