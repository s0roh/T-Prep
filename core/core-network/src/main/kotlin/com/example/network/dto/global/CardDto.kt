package com.example.network.dto.global

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Представляет карточку для системы интервального повторения
 *
 * @property id Уникальный идентификатор карточки
 * @property question Вопрос, отображаемый пользователю
 * @property answer Правильный ответ на вопрос
 * @property attachment Ссылка или идентификатор вложения
 * @property otherAnswers Контейнер с неправильными вариантами ответов
 */
@Serializable
data class CardDto(
    @SerialName("local_id") val id: Int,
    @SerialName("question") val question: String,
    @SerialName("answer") val answer: String,
    @SerialName("attachment") val attachment: String,
    @SerialName("other_answers") val otherAnswers: OtherAnswersDto,
)