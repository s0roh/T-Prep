package com.example.decks.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import com.example.database.TPrepDatabase
import com.example.database.models.Source
import com.example.decks.data.mapper.toEntity
import com.example.decks.domain.repository.PublicDeckRepository
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PublicDeckRepositoryImpl @Inject internal constructor(
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
    private val database: TPrepDatabase,
) : PublicDeckRepository {

    override suspend fun getDeckById(id: String): Pair<Deck, Source> {
        val localDeck = database.deckDao.getDeckByServerId(serverDeckId = id)?.let { deckDbo ->
            val cards = database.cardDao.getCardsForDeck(deckDbo.id).first().map { it.toEntity() }
            deckDbo.toEntity(cards)
        }

        return if (localDeck != null) {
            localDeck to Source.LOCAL
        } else authRequestWrapper.executeWithAuth { token ->
            apiService.getDeckById(id, token).toEntity() to Source.NETWORK
        }
    }

    override fun getPublicDecks(query: String?): Flow<PagingData<DeckUiModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = ENABLE_PLACEHOLDERS,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = INITIAL_LOAD_SIZE
            ),
            pagingSourceFactory = {
                PublicDecksPagingSource(
                    apiService = apiService,
                    authRequestWrapper = authRequestWrapper,
                    query = query
                )
            }
        ).flow
    }

    companion object {

        const val PAGE_SIZE = 10
        const val ENABLE_PLACEHOLDERS = false
        const val PREFETCH_DISTANCE = 7
        const val INITIAL_LOAD_SIZE = 10
    }
}