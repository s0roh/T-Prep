package com.example.network.dto.global

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a flashcard used in the spaced repetition system, specifically for data transfer.
 *
 * This DTO is used for serializing and deserializing the card data when transferring between the server and the client.
 *
 * @property id Unique identifier for the card.
 * @property question The question displayed to the user.
 * @property answer The correct answer to the question.
 */
@Serializable
data class CardDto(
    @SerialName("local_id") val id: Int,
    @SerialName("question") val question: String,
    @SerialName("answer") val answer: String
)