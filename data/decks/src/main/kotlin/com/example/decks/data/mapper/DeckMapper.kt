package com.example.decks.data.mapper

import com.example.decks.domain.entity.Card
import com.example.decks.domain.entity.Deck
import com.example.network.dto.global.CardDto
import com.example.network.dto.global.DeckDto

internal fun DeckDto.toEntity(): Deck =
    Deck(
        id = id,
        name = name,
        isPublic = isPublic,
        cards = cards.map { it.toEntity() }
    )

internal fun CardDto.toEntity(): Card =
    Card(
        id = this.id,
        question = this.question,
        answer = this.answer
    )