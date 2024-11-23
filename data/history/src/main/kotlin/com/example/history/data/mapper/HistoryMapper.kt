package com.example.history.data.mapper

import com.example.database.models.HistoryDBO
import com.example.database.models.Source
import com.example.history.domain.entity.TrainingHistory

internal fun HistoryDBO.toEntity(): TrainingHistory =
    TrainingHistory(
        id = id,
        deckId = deckId,
        cardId = cardId,
        timestamp = timestamp,
        isCorrect = isCorrect
    )

internal fun TrainingHistory.toDBO(source: Source): HistoryDBO =
    HistoryDBO(
        id = id,
        deckId = deckId,
        cardId = cardId,
        timestamp = timestamp,
        isCorrect = isCorrect,
        source = source
    )