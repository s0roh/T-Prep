package com.example.common.domain.entity


/**
 * Представляет собой карточку.
 *
 * Карточка содержит вопрос, отображаемый пользователю, и правильный ответ на него.
 *
 * @property id Уникальный идентификатор карточки.
 * @property question Вопрос, отображаемый пользователю.
 * @property answer Правильный ответ на вопрос.
 * @property wrongAnswers Список неправильных ответов (используется, например, в режиме выбора).
 * @property attachment Идентификатор вложения, который можно использовать для загрузки с сервера.
 * @property picturePath Путь к изображению, связанному с карточкой (может отсутствовать).
 */
data class Card(
    val id: Int,
    val question: String,
    val answer: String,
    val wrongAnswers: List<String> = emptyList(),
    val attachment: String? = null,
    val picturePath: String? = null,
)