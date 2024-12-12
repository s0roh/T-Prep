package com.example.training.domain

import com.example.common.domain.entity.Card
import com.example.database.models.Source

interface TrainingRepository {

    suspend fun prepareTrainingCards(deckId: String, cards: List<Card>, source: Source): List<Card>

    suspend fun recordAnswer(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        cardId: Int,
        isCorrect: Boolean,
        source: Source
    )
}