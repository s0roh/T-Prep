package com.example.network.dto.global

import com.example.network.dto.collection.DeckDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the response containing a list of public decks available on the server.
 *
 * This DTO is used to transfer data about public decks, including the number of decks and the actual deck data.
 *
 * @property count The total number of public decks available.
 * @property decks A list of [com.example.network.dto.collection.DeckDto] objects representing the public decks. It can be null if no decks are available.
 */
@Serializable
data class PublicDecksDto(
    @SerialName("count") val count: Int,
    @SerialName("items") val decks: List<DeckDto>? = null
)