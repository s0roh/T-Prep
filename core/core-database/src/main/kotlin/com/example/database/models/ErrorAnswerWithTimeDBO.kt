package com.example.database.models

data class ErrorAnswerWithTimeDBO(
    val id: Long,
    val trainingSessionId: String,
    val trainingSessionTime: Long,
    val cardId: Int,
    val deckId: String,
    val question: String,
    val answer: String,
    val userAnswer: String,
    val blankAnswer: String?,
    val trainingMode: TrainingMode,
    val attachment: String?,
)