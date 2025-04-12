package com.example.feature.decks.domain.usecase

import com.example.history.domain.repository.HistoryRepository
import javax.inject.Inject

internal class GetDeckTrainingStatsUseCase @Inject constructor(
    private val repository: HistoryRepository,
) {

    suspend operator fun invoke(deckId: String): List<Double> =
        repository.getDeckTrainingStats(deckId = deckId).map { it.coerceAtMost(100.0) }
}