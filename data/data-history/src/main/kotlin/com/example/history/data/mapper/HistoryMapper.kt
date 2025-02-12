package com.example.history.data.mapper

import com.example.database.models.HistoryDBO
import com.example.history.domain.entity.TrainingHistory

internal fun HistoryDBO.toEntity(incorrectAnswer: String? = null): TrainingHistory =
    TrainingHistory(
        id = id,
        deckId = deckId,
        deckName = deckName,
        cardsCount = cardsCount,
        cardId = cardId,
        timestamp = timestamp,
        isCorrect = isCorrect,
        incorrectAnswer = incorrectAnswer,
        source = source,
        userID = userId,
        trainingSessionId = trainingSessionId

    )