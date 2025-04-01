package com.example.feature.decks.domain.entity

data class PublicDeckFilters(
    val query: String? = null,
    val sortBy: String? = null,
    val category: String? = null
)
