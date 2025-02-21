package com.example.training.data.mapper

import com.example.database.models.ErrorDBO
import com.example.database.models.TrainingMode
import com.example.database.models.TrainingModesHistoryDBO
import com.example.training.domain.entity.TrainingError
import com.example.training.domain.entity.TrainingModes

internal fun ErrorDBO.toEntity(
    trainingSessionTime: Long,
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

internal fun TrainingModesHistoryDBO.toEntity(): TrainingModes =
    TrainingModes(
        deckId = deckId,
        modes = buildList {
            if (multipleChoiceEnabled) add(TrainingMode.MULTIPLE_CHOICE)
            if (trueFalseEnabled) add(TrainingMode.TRUE_FALSE)
            if (fillInTheBlankEnabled) add(TrainingMode.FILL_IN_THE_BLANK)
        }
    )

internal fun TrainingModes.toDbo(): TrainingModesHistoryDBO =
    TrainingModesHistoryDBO(
        deckId = deckId,
        multipleChoiceEnabled = modes.contains(TrainingMode.MULTIPLE_CHOICE),
        trueFalseEnabled = modes.contains(TrainingMode.TRUE_FALSE),
        fillInTheBlankEnabled = modes.contains(TrainingMode.FILL_IN_THE_BLANK)
    )