package com.example.decks.domain.repository

import com.example.common.domain.entity.Deck
import kotlinx.coroutines.flow.SharedFlow

interface PublicDeckRepository {

    fun getPublicDecksFlow(): SharedFlow<List<Deck>>

    suspend fun getDeckById(id: Long): Deck

    suspend fun loadNextPublicDecks()
}