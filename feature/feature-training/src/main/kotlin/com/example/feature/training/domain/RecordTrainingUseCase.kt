package com.example.feature.training.domain

import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

internal class RecordTrainingUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        source: Source,
        trainingSessionId: String,
    ) = repository.recordTraining(
        deckId = deckId,
        deckName = deckName,
        cardsCount = cardsCount,
        source = source,
        trainingSessionId = trainingSessionId
    )
}