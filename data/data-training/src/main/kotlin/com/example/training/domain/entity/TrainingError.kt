package com.example.training.domain.entity

import com.example.database.models.TrainingMode

/**
 * Класс, представляющий ошибку, совершённую пользователем при тренировке.
 *
 * @param id Идентификатор ошибки.
 * @param cardId Идентификатор карточки, с которой была допущена ошибка.
 * @param deckId Идентификатор колоды.
 * @param trainingSessionId Идентификатор сессии тренировки.
 * @param trainingSessionTime Время тренировки в миллисекундах.
 * @param question Вопрос на карточке.
 * @param answer Правильный ответ на карточке.
 * @param blankAnswer Ответ, если используется режим "Заполни пропуск".
 * @param userAnswer Ответ пользователя.
 * @param trainingMode Режим тренировки, в котором была допущена ошибка.
 * @param attachment Дополнительный атрибут (например, изображение) для ошибки.
 */
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
