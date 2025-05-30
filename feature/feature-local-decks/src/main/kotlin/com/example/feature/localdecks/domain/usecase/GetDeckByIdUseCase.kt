package com.example.feature.localdecks.domain.usecase

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class GetDeckByIdUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(deckId: String): Deck? = repository.getDeckById(deckId)
}