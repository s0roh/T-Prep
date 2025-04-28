package com.example.localdecks.domain.entity

/**
 * Модель запроса для создания или обновления колоды.
 *
 * @param name Название колоды.
 * @param isPublic Признак публичности колоды (true — публичная, false — приватная).
 */
data class DeckRequest(
    val name: String,
    val isPublic: Boolean,
)
