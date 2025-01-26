package com.example.history.data.mapper

import com.example.database.models.HistoryDBO
import com.example.history.domain.entity.TrainingHistory

internal fun HistoryDBO.toEntity(): TrainingHistory =
    TrainingHistory(
        id = id,
        deckId = deckId,
        deckName = deckName,
        cardsCount = cardsCount,
        cardId = cardId,
        timestamp = timestamp,
        isCorrect = isCorrect,
        source = source,
        coefficient = coefficient,
        userID = userId
    )

internal fun TrainingHistory.toDBO(): HistoryDBO =
    HistoryDBO(
        id = id,
        deckId = deckId,
        deckName = deckName,
        cardsCount = cardsCount,
        cardId = cardId,
        timestamp = timestamp,
        isCorrect = isCorrect,
        source = source,
        coefficient = coefficient,
        userId = userID
    )