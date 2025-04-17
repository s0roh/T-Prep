package com.example.training.data.mapper

import com.example.database.models.ErrorAnswerWithTimeDBO
import com.example.database.models.TrainingMode
import com.example.database.models.TrainingModesHistoryDBO
import com.example.training.domain.entity.TrainingError
import com.example.training.domain.entity.TrainingModes

internal fun ErrorAnswerWithTimeDBO.toEntity(): TrainingError =
    TrainingError(
        id = id,
        cardId = cardId,
        deckId = deckId,
        trainingSessionId = trainingSessionId,
        trainingSessionTime = trainingSessionTime,
        question = question,
        answer = answer,
        blankAnswer = blankAnswer,
        userAnswer = userAnswer,
        trainingMode = trainingMode,
        attachment = attachment
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