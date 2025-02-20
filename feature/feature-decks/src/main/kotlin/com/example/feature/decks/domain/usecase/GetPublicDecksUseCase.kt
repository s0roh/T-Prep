package com.example.feature.decks.domain.usecase

import androidx.paging.PagingData
import com.example.decks.domain.entity.PublicDeck
import com.example.decks.domain.repository.PublicDeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetPublicDecksUseCase @Inject constructor(
    private val repository: PublicDeckRepository
) {

    operator fun invoke(): Flow<PagingData<PublicDeck>> = repository.getPublicDecks()
}