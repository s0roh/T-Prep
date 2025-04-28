package com.example.history.domain.entity

import com.example.database.models.TrainingMode

/**
 * Статистика по режиму тренировки.
 *
 * @property modeName Название режима тренировки.
 * @property totalAttempts Общее количество попыток в этом режиме.
 * @property correctAttempts Количество правильных попыток в этом режиме.
 * @property incorrectAttempts Количество неправильных попыток в этом режиме.
 */
data class TrainingModeStats(
    val modeName: TrainingMode,
    val totalAttempts: Double,
    val correctAttempts: Double,
    val incorrectAttempts: Double,
)
