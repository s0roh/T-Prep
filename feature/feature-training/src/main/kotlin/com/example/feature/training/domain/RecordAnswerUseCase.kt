package com.example.feature.training.domain

import com.example.database.models.TrainingMode
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

internal class RecordAnswerUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(
        cardId: Int,
        isCorrect: Boolean,
        question: String,
        correctAnswer: String,
        fillInTheBlankAnswer: String? = null,
        incorrectAnswer: String? = null,
        trainingSessionId: String,
        trainingMode: TrainingMode,
        attachment: String? = null,
    ) = repository.recordAnswer(
        cardId = cardId,
        isCorrect = isCorrect,
        question = question,
        correctAnswer = correctAnswer,
        fillInTheBlankAnswer = fillInTheBlankAnswer,
        incorrectAnswer = incorrectAnswer,
        trainingSessionId = trainingSessionId,
        trainingMode = trainingMode,
        attachment = attachment
    )
}