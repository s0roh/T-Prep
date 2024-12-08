package com.example.training.domain

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

class GetDeckByIdLocalUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(deckId: Long): Deck = repository.getDeckById(deckId)
        ?: throw IllegalStateException("Deck with id $deckId not found")
}
