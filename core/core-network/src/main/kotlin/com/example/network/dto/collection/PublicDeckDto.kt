package com.example.network.dto.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO, представляющий публичную колоду.
 *
 * Содержит информацию о конкретной публичной колоде, включая её идентификатор,
 * название, статус публичности и количество карт в ней.
 *
 * @property id Уникальный идентификатор колоды.
 * @property name Название колоды.
 * @property isPublic Флаг, указывающий, является ли колода публичной.
 * @property cardsCount Количество карт в данной колоде.
 */

@Serializable
data class PublicDeckDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("is_public") val isPublic: Boolean,
    @SerialName("cards_count") val cardsCount: Int
)
