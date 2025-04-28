package com.example.localdecks.domain.entity

/**
 * Модель запроса для создания или обновления карточки.
 *
 * @param question Текст вопроса.
 * @param answer Текст правильного ответа.
 * @param wrongAnswers Список неправильных ответов (может быть пустым или содержать до 3-х элементов).
 */
data class CardRequest(
    val question: String,
    val answer: String,
    val wrongAnswers: List<String>,
)