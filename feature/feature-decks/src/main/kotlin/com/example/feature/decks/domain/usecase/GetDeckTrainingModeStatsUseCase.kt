package com.example.feature.decks.domain.usecase

import com.example.history.domain.entity.TrainingModeStats
import com.example.history.domain.repository.HistoryRepository
import javax.inject.Inject

internal class GetDeckTrainingModeStatsUseCase @Inject constructor(
    private val repository: HistoryRepository,
) {

    suspend operator fun invoke(deckId: String): List<TrainingModeStats> =
        repository.getDeckTrainingModeStats(deckId = deckId)
}