package com.example.decks.data.mapper

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.network.dto.global.CardDto
import com.example.network.dto.collection.DeckDto

internal fun DeckDto.toEntity(): Deck =
    Deck(
        id = id,
        name = name,
        isPublic = isPublic,
        cards = cards.map { it.toEntity() }
    )

internal fun CardDto.toEntity(): Card =
    Card(
        id = id,
        question = question,
        answer = answer
    )


