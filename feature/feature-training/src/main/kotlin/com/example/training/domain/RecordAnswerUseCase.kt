package com.example.training.domain

import com.example.common.domain.entity.TrainingMode
import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

internal class RecordAnswerUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        cardId: Int,
        isCorrect: Boolean,
        incorrectAnswer: String? = null,
        source: Source,
        trainingSessionId: String,
        trainingMode: TrainingMode
    ) =
        repository.recordAnswer(
            deckId = deckId,
            deckName = deckName,
            cardsCount = cardsCount,
            cardId = cardId,
            isCorrect = isCorrect,
            incorrectAnswer = incorrectAnswer,
            source = source,
            trainingSessionId = trainingSessionId,
            trainingMode = trainingMode
        )
}