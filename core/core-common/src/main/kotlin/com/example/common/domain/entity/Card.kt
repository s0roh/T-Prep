package com.example.common.domain.entity

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
data class Card(
    val id: Int,
    val trainingMode: TrainingMode? = null,
    val question: String,
    val answer: String,
    val wrongAnswers: List<String> = emptyList(),
    val displayedAnswer: String? = null,
    val partialAnswer: String? = null,
    val missingWords: List<String> = emptyList()
)

/**
 * Перечисление режимов тренировки для карточек.
 *
 * @property MULTIPLE_CHOICE Режим "Множественный выбор", где нужно выбрать правильный ответ из нескольких вариантов.
 * @property TRUE_FALSE Режим "Правильно или нет", где пользователь должен выбрать правильный или неправильный ответ.
 * @property FILL_IN_THE_BLANK Режим "Дополнить ответ", где нужно дополнить пропущенные части ответа.
 */
enum class TrainingMode {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    FILL_IN_THE_BLANK
}