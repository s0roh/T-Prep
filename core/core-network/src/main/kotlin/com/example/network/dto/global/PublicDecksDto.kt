package com.example.network.dto.global

import com.example.network.dto.collection.PublicDeckDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO, представляющий ответ с сервера, содержащий список публичных колод.
 *
 * @property count Количество полученных колод.
 * @property decks Список публичных колод в виде объектов [PublicDeckDto].
 * Может быть `null`, если доступных колод нет.
 */
@Serializable
data class PublicDecksDto(
    @SerialName("count") val count: Int,
    @SerialName("items") val decks: List<PublicDeckDto>? = null
)