package com.example.history.domain.entity

data class TrainingHistoryItem(
    val timestamp: Long,
    val percentOfCorrectAnswers: Int,
    val trainingHistories: List<TrainingHistory>,
)