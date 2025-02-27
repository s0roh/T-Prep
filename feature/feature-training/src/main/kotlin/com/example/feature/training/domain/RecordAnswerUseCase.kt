package com.example.feature.training.domain

import com.example.database.models.Source
import com.example.database.models.TrainingMode
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
        question: String,
        correctAnswer: String,
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
            question = question,
            correctAnswer = correctAnswer,
            incorrectAnswer = incorrectAnswer,
            source = source,
            trainingSessionId = trainingSessionId,
            trainingMode = trainingMode
        )
}