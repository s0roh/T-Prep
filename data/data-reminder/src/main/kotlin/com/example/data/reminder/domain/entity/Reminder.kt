package com.example.data.reminder.domain.entity

import com.example.database.models.Source

/**
 * Модель данных для напоминания о тренировке.
 *
 * @property id Уникальный идентификатор напоминания (по умолчанию 0 для новых объектов).
 * @property reminderTime Время напоминания.
 * @property name Название напоминания.
 * @property source Источник колоды для напоминаная [Source].
 * @property deckId Идентификатор колоды, связанной с напоминанием.
 */
data class Reminder(
    val id: Long = 0,
    val reminderTime: Long,
    val name: String,
    val source: Source,
    val deckId: String
)