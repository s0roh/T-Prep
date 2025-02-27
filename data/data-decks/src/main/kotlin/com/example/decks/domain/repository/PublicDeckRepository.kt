package com.example.decks.domain.repository

import androidx.paging.PagingData
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import kotlinx.coroutines.flow.Flow

interface PublicDeckRepository {

    fun getPublicDecks(query: String? = null): Flow<PagingData<DeckUiModel>>

    suspend fun getDeckById(id: String): Deck
}