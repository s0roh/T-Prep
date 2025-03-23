package com.example.feature.decks.domain.usecase

import com.example.decks.domain.repository.DeckDetailsRepository
import javax.inject.Inject

internal class GetNextTrainingTimeUseCase @Inject constructor(
    private val repository: DeckDetailsRepository
) {

    suspend operator fun invoke(deckId: String): Long? =
        repository.getNextTrainingTime(deckId = deckId)
}