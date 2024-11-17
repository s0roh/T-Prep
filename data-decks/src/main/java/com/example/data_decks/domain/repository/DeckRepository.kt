package com.example.data_decks.domain.repository

import com.example.data_decks.domain.entity.Deck
import kotlinx.coroutines.flow.SharedFlow

interface DeckRepository {

    fun getPublicDecksFlow(): SharedFlow<List<Deck>>

    suspend fun getDeckById(id: Long): Deck

    suspend fun loadNextPublicDecks()
}