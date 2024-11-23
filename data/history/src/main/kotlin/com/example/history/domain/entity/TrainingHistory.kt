package com.example.history.domain.entity

data class TrainingHistory(
    val id: Long,
    val deckId: Long,
    val cardId: Long,
    val timestamp: Long,
    val isCorrect: Boolean
)
