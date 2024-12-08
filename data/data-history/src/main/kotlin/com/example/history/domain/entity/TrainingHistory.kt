package com.example.history.domain.entity

import com.example.database.models.Source

data class TrainingHistory(
    val id: Long,
    val deckId: Long,
    val deckName: String,
    val cardsCount: Int,
    val cardId: Long,
    val timestamp: Long,
    val isCorrect: Boolean,
    val source: Source,
    val coefficient: Double
)
