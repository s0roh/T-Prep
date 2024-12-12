package com.example.network.dto.collection

import com.example.network.dto.global.CardDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Represents a deck of flashcards used in the spaced repetition system, specifically for data transfer.
 *
 * This DTO is used for serializing and deserializing deck data when transferring between the server and the client.
 *
 * @property id Unique identifier for the deck.
 * @property name The name of the deck.
 * @property isPublic Flag indicating whether the deck is public or private.
 * @property cards List of cards that belong to this deck. Each card is represented by [CardDto].
 */
@Serializable
data class DeckDto(
    @SerialName("id") val id: String = UUID.randomUUID().toString(),
    @SerialName("name") val name: String,
    @SerialName("is_public") val isPublic: Boolean,
    @SerialName("cards") val cards: List<CardDto>
)