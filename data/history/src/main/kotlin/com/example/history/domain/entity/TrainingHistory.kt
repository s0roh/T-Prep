package com.example.history.domain.entity

import com.example.database.models.Source

data class TrainingHistory(
    val id: Long,
    val deckId: Long,
    val cardId: Long,
    val timestamp: Long,
    val isCorrect: Boolean,
    val source: Source
)
