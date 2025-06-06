package com.example.feature.localdecks.domain.usecase

import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class DeleteDeckUseCase @Inject constructor(
    private val repository: LocalDeckRepository,
) {

    suspend operator fun invoke(deckId: String) = repository.deleteDeck(deckId = deckId)
}