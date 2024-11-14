package com.example.data_decks.domain.repository

import com.example.data_decks.domain.entity.Deck
import kotlinx.coroutines.flow.StateFlow

interface DeckRepository {

    fun getPublicDecksFlow(): StateFlow<List<Deck>>

    suspend fun loadNextPublicDecks()
}