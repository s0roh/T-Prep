package com.example.common.ui.entity

data class DeckUiModel(
    val id: String,
    val name: String,
    val isPublic: Boolean,
    val isLiked: Boolean,
    val shouldShowLikes: Boolean,
    val cardsCount: Int,
    val likes: Int
)
