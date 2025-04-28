package com.example.history.domain.entity

/**
 * Представляет элемент истории тренировки с процентом правильных ответов.
 *
 * @property timestamp Время выполнения тренировки в формате UNIX timestamp.
 * @property percentOfCorrectAnswers Процент правильных ответов.
 * @property trainingHistories Список историй тренировок для каждой карточки.
 */
data class TrainingHistoryItem(
    val timestamp: Long,
    val percentOfCorrectAnswers: Int,
    val trainingHistories: List<TrainingHistory>,
)