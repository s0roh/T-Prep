package com.example.decks.domain.repository

import androidx.paging.PagingData
import com.example.common.domain.entity.Deck
import com.example.decks.domain.entity.PublicDeck
import kotlinx.coroutines.flow.Flow

interface PublicDeckRepository {

    fun getPublicDecks(): Flow<PagingData<PublicDeck>>

    suspend fun getDeckById(id: String): Deck
}