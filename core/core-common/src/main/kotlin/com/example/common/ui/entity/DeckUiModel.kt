package com.example.common.ui.entity

/**
 * UI-модель колоды, используемая для отображения в пользовательском интерфейсе.
 *
 * Используется для отображения информации о колоде на экране, включая статистику и статус.
 *
 * @property id Уникальный идентификатор колоды.
 * @property name Название колоды.
 * @property isPublic Флаг, указывающий, является ли колода публичной.
 * @property isLiked Флаг, указывающий, поставил ли текущий пользователь лайк этой колоде.
 * @property shouldShowLikes Флаг, определяющий, нужно ли отображать количество лайков.
 * @property cardsCount Количество карточек в колоде.
 * @property likes Количество лайков, полученных колодой.
 * @property trainings Количество тренировок, проведённых с этой колодой.
 */
data class DeckUiModel(
    val id: String,
    val name: String,
    val isPublic: Boolean,
    val isLiked: Boolean,
    val shouldShowLikes: Boolean,
    val cardsCount: Int,
    val likes: Int,
    val trainings: Int,
)
