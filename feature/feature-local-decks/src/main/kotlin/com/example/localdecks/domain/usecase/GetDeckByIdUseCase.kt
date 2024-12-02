package com.example.localdecks.domain.usecase

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

class GetDeckByIdUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(deckId: Long): Deck? = repository.getDeckById(deckId)
}