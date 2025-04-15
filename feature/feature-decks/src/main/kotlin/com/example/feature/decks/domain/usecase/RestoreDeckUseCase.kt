package com.example.feature.decks.domain.usecase

import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class RestoreDeckUseCase @Inject constructor(
    private val repository: LocalDeckRepository,
) {

    suspend operator fun invoke(deckId: String) = repository.restoreDeck(deckId = deckId)
}