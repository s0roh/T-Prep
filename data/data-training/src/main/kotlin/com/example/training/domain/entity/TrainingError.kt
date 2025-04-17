package com.example.training.domain.entity

import com.example.database.models.TrainingMode

data class TrainingError(
    val id: Long,
    val cardId: Int,
    val deckId: String,
    val trainingSessionId: String,
    val trainingSessionTime: Long,
    val question: String,
    val answer: String,
    val blankAnswer: String? = null,
    val userAnswer: String,
    val trainingMode: TrainingMode,
    val attachment: String? = null,
)
