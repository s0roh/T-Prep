package com.example.decks.domain.usecase

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

class GetDeckByIdFromLocalUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(deckId: String): Deck? = repository.getDeckById(deckId)
}