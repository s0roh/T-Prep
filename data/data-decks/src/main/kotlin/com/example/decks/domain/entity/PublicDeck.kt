package com.example.decks.domain.entity

data class PublicDeck(
    val id: String,
    val name: String,
    val isPublic: Boolean,
    val cardsCount: Int
)
