package com.example.decks.domain.repository

import androidx.paging.PagingData
import com.example.common.domain.entity.Deck
import kotlinx.coroutines.flow.Flow

interface PublicDeckRepository {

    fun getPublicDecks(): Flow<PagingData<Deck>>

    suspend fun getDeckById(id: String): Deck
}