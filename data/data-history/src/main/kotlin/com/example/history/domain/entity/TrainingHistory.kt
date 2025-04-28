package com.example.history.domain.entity

import com.example.database.models.Source
import com.example.database.models.TrainingMode

/**
 * Представляет историю тренировки для одной карточки.
 *
 * @property id Уникальный идентификатор записи о тренировке.
 * @property deckId Идентификатор колоды, с которой была проведена тренировка.
 * @property deckName Название колоды.
 * @property cardsCount Количество карточек, использованных в тренировке.
 * @property cardId Идентификатор карточки, с которой была проведена тренировка.
 * @property timestamp Время проведения тренировки.
 * @property isCorrect Флаг, указывающий, был ли ответ правильным.
 * @property trainingMode Режим тренировки, в котором проводилась тренировка.
 * @property source Источник колоды.
 * @property userID Идентификатор пользователя, который проходил тренировку.
 * @property trainingSessionId Идентификатор сессии тренировки.
 */
data class TrainingHistory(
    val id: Long,
    val deckId: String,
    val deckName: String,
    val cardsCount: Int,
    val cardId: Int,
    val timestamp: Long,
    val isCorrect: Boolean,
    val trainingMode: TrainingMode,
    val source: Source,
    val userID: String,
    val trainingSessionId: String
)
