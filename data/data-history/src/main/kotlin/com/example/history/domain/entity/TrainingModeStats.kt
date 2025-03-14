package com.example.history.domain.entity

import com.example.database.models.TrainingMode

data class TrainingModeStats(
    val modeName: TrainingMode,
    val totalAttempts: Double,
    val correctAttempts: Double,
    val incorrectAttempts: Double,
)
