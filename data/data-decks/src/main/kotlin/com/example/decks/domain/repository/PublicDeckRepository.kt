package com.example.decks.domain.repository

import androidx.paging.PagingData
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import com.example.database.models.Source
import kotlinx.coroutines.flow.Flow

interface PublicDeckRepository {

    fun getPublicDecks(
        query: String? = null,
        sortBy: String? = null,
        category: String? = null,
    ): Flow<PagingData<DeckUiModel>>

    suspend fun getDeckById(id: String): Pair<Deck, Source>

    suspend fun likeOrUnlikeDeck(deckId: String, isLiked: Boolean): Int

    suspend fun getFavouriteDeckIds(): List<String>
}