package com.example.training.domain.entity

import com.example.database.models.TrainingMode

data class TrainingError(
    val id: Long,
    val trainingSessionId: String,
    val trainingSessionTime: Long,
    val question: String,
    val correctAnswer: String,
    val incorrectAnswer: String,
    val trainingMode: TrainingMode,
)
