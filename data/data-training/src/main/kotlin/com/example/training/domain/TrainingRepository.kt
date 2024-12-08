package com.example.training.domain

import com.example.common.domain.entity.Card
import com.example.database.models.Source

interface TrainingRepository {

    suspend fun prepareTrainingCards(deckId: Long, cards: List<Card>, source: Source): List<Card>

    suspend fun recordAnswer(
        deckId: Long,
        deckName: String,
        cardsCount: Int,
        cardId: Long,
        isCorrect: Boolean,
        source: Source
    )
}