package com.example.training.domain.entity

data class TrainingError(
    val id: Long,
    val trainingSessionId: String,
    val trainingSessionTime: Long,
    val correctAnswer: String,
    val incorrectAnswer: String,
)
