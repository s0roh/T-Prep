package com.example.common.domain.entity

import java.util.UUID

/**
 * Represents a deck of flashcards used in the spaced repetition system.
 *
 * A Deck contains information about the deck itself and the cards associated with it.
 * This is the business model used for operations in the application, such as displaying deck information to the user
 * and managing cards within the deck.
 *
 * @property id Unique identifier for the deck.
 * @property name The name of the deck.
 * @property isPublic Flag indicating whether the deck is public or private.
 * @property cards List of [Card] objects that belong to this deck.
 */
data class Deck(
    val id: String,
    val name: String,
    val isPublic: Boolean,
    val cards: List<Card>,
)