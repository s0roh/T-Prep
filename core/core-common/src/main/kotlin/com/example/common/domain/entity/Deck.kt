package com.example.common.domain.entity

/**
 * Представляет собой колоду карточек.
 *
 * Колода содержит информацию о себе и связанных с ней карточках.
 *
 * @property id Уникальный идентификатор колоды.
 * @property name Название колоды.
 * @property isPublic Флаг, указывающий, является ли колода публичной или приватной.
 * @property authorId Идентификатор автора колоды (может отсутствовать для локальных колод).
 * @property cards Список объектов [Card], принадлежащих этой колоде.
 */
data class Deck(
    val id: String,
    val name: String,
    val isPublic: Boolean,
    val authorId: String? = null,
    val cards: List<Card>,
)