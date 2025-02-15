package com.example.training.data.mapper

import com.example.common.domain.entity.TrainingMode
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

internal fun TrainingMode.toDbo(): com.example.database.models.TrainingMode {
    return when (this) {
        TrainingMode.MULTIPLE_CHOICE -> com.example.database.models.TrainingMode.MULTIPLE_CHOICE
        TrainingMode.TRUE_FALSE -> com.example.database.models.TrainingMode.TRUE_FALSE
        TrainingMode.FILL_IN_THE_BLANK -> com.example.database.models.TrainingMode.FILL_IN_THE_BLANK
    }
}