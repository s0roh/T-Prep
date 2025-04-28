package com.example.training.domain.entity

import com.example.database.models.TrainingMode

/**
 * Класс, представляющий режимы тренировки для конкретной колоды.
 *
 * @param deckId Идентификатор колоды.
 * @param modes Список режимов тренировки, доступных для этой колоды.
 */

data class TrainingModes(
    val deckId: String,
    val modes: List<TrainingMode>
)
