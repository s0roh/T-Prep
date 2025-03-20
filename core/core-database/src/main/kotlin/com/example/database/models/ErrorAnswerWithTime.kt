package com.example.database.models

data class ErrorAnswerWithTime(
    val id: Long,
    val trainingSessionId: String,
    val trainingSessionTime: Long,
    val cardId: Int,
    val question: String,
    val answer: String,
    val userAnswer: String,
    val blankAnswer: String?,
    val trainingMode: TrainingMode
)