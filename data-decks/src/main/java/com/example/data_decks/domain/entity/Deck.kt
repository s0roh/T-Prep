package com.example.data_decks.domain.entity

data class Deck(
    val id: Long,
    val name: String,
    val isPublic: Boolean,
    val cards: List<Card>
)
