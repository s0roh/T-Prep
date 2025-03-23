package com.example.history.data.mapper

import com.example.database.models.HistoryDBO
import com.example.database.models.TrainingMode
import com.example.history.domain.entity.TrainingHistory

internal fun HistoryDBO.toEntity(
    isCorrect: Boolean,
    trainingMode: TrainingMode,
    cardId: Int,
): TrainingHistory =
    TrainingHistory(
        id = id,
        userID = userId,
        deckId = deckId,
        deckName = deckName,
        cardsCount = cardsCount,
        timestamp = timestamp,
        source = source,
        trainingSessionId = trainingSessionId,
        isCorrect = isCorrect,
        trainingMode = trainingMode,
        cardId = cardId
    )