package com.example.history.domain.entity

import com.example.database.models.Source

data class TrainingHistory(
    val id: Long,
    val deckId: String,
    val deckName: String,
    val cardsCount: Int,
    val cardId: Int,
    val timestamp: Long,
    val isCorrect: Boolean,
    val source: Source,
    val coefficient: Double
)
