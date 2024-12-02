package com.example.decks.domain.usecase

import androidx.paging.PagingData
import com.example.common.domain.entity.Deck
import com.example.decks.domain.repository.PublicDeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetPublicDecksUseCase @Inject constructor(
    private val repository: PublicDeckRepository
) {

    operator fun invoke(): Flow<PagingData<Deck>> = repository.getPublicDecks()
}