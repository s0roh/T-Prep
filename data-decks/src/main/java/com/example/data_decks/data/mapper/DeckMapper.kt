package com.example.data_decks.data.mapper

import com.example.core_network.dto.global.CardDto
import com.example.core_network.dto.global.DeckDto
import com.example.data_decks.domain.entity.Card
import com.example.data_decks.domain.entity.Deck

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