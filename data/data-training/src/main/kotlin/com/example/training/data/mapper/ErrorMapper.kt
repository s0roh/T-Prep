package com.example.training.data.mapper

import com.example.database.models.ErrorDBO
import com.example.training.domain.entity.TrainingError

internal fun ErrorDBO.toEntity(
    trainingSessionTime: Long,
    question: String,
    correctAnswer: String
): TrainingError =
    TrainingError(
        id = id,
        trainingSessionId = trainingSessionId,
        trainingSessionTime = trainingSessionTime,
        question = question,
        correctAnswer = correctAnswer,
        incorrectAnswer = incorrectAnswer,
        trainingMode = trainingMode
    )