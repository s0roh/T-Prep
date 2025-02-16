package com.example.common.domain.entity


/**
 * Represents a flashcard used in the spaced repetition system.
 *
 * A Card contains the question displayed to the user and the correct answer. It is used
 * during the spaced repetition training to assess how well the user remembers the material.
 *
 * @property id Unique identifier for the card.
 * @property question The question displayed to the user.
 * @property answer The correct answer to the question.
 */
data class Card(
    val id: Int,
    val question: String,
    val answer: String,
    val wrongAnswers: List<String> = emptyList()
)