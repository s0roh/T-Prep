package com.example.feature.decks.domain.usecase

import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class SoftDeleteDeckUseCase @Inject constructor(
    private val repository: LocalDeckRepository,
) {

    suspend operator fun invoke(deckId: String) = repository.softDeleteDeck(deckId = deckId)
}