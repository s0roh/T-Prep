package com.example.feature.decks.domain.usecase

import androidx.paging.PagingData
import com.example.common.ui.entity.DeckUiModel
import com.example.decks.domain.repository.PublicDeckRepository
import com.example.feature.decks.domain.entity.PublicDeckFilters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

internal class GetPublicDecksUseCase @Inject constructor(
    private val repository: PublicDeckRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(filtersFlow: StateFlow<PublicDeckFilters>): Flow<PagingData<DeckUiModel>> {
        return filtersFlow.flatMapLatest { filters ->
            repository.getPublicDecks(
                query = filters.query,
                sortBy = filters.sortBy,
                category = filters.category
            )
        }
    }
}