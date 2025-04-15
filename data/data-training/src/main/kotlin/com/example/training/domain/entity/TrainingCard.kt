package com.example.training.domain.entity

import com.example.database.models.TrainingMode

/**
 * Представляет карточку для тренировок в системе интервального повторения.
 *
 * Карточка содержит вопрос, отображаемый пользователю, и правильный ответ. Она используется
 * в процессе тренировки, чтобы оценить, как хорошо пользователь запомнил материал.
 *
 * @property id Уникальный идентификатор карточки.
 * @property trainingMode Режим тренировки, который определяет тип карточки (множественный выбор, правда/неправда, дополнить ответ).
 * @property question Вопрос, отображаемый пользователю.
 * @property answer Правильный ответ на вопрос.
 * @property wrongAnswers Список неверных ответов для режима множественного выбора (по умолчанию пустой).
 * @property displayedAnswer Отображаемый ответ в режиме "Правильно или нет".
 * @property partialAnswer Частичный ответ в режиме "Дополнить ответ", где часть ответа скрыта.
 * @property missingWords Список слов, которые необходимо дополнить в режиме "Дополнить ответ".
 */
data class TrainingCard(
    val id: Int,
    val trainingMode: TrainingMode? = null,
    val question: String,
    val answer: String,
    val wrongAnswers: List<String> = emptyList(),
    val displayedAnswer: String? = null,
    val partialAnswer: String? = null,
    val attachment: String? = null,
    val missingWords: List<String> = emptyList()
)
