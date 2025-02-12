package com.example.training.data.mapper

import com.example.database.models.ErrorDBO
import com.example.training.domain.entity.TrainingError

internal fun ErrorDBO.toEntity(trainingSessionTime: Long, correctAnswer: String): TrainingError =
    TrainingError(
        id = id,
        trainingSessionId = trainingSessionId,
        trainingSessionTime = trainingSessionTime,
        correctAnswer = correctAnswer,
        incorrectAnswer = incorrectAnswer
    )