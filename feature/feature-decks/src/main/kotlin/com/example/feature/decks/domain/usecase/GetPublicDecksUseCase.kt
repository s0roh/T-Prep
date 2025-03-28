package com.example.feature.decks.domain.usecase

import androidx.paging.PagingData
import com.example.common.ui.entity.DeckUiModel
import com.example.decks.domain.repository.PublicDeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetPublicDecksUseCase @Inject constructor(
    private val repository: PublicDeckRepository
) {

    operator fun invoke(): Flow<PagingData<DeckUiModel>> = repository.getPublicDecks()
}