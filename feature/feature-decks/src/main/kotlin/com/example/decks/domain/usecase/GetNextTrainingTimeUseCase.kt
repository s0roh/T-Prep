package com.example.decks.domain.usecase

import com.example.database.models.Source
import com.example.decks.domain.repository.DeckDetailsRepository
import javax.inject.Inject

internal class GetNextTrainingTimeUseCase @Inject constructor(
    private val repository: DeckDetailsRepository
) {

    suspend operator fun invoke(deckId: String, source: Source): Long? =
        repository.getNextTrainingTime(deckId = deckId, source = source)
}