package com.example.decks.data.mapper

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.decks.domain.entity.PublicDeck
import com.example.network.dto.global.CardDto
import com.example.network.dto.collection.DeckDto
import com.example.network.dto.collection.PublicDeckDto

internal fun DeckDto.toEntity(): Deck =
    Deck(
        id = id,
        name = name,
        isPublic = isPublic,
        cards = cards.map { it.toEntity() }
    )

internal fun PublicDeckDto.toEntity(): PublicDeck =
    PublicDeck(
        id = id,
        name = name,
        isPublic = isPublic,
        cardsCount = cardsCount
    )

internal fun CardDto.toEntity(): Card =
    Card(
        id = id,
        question = question,
        answer = answer
    )


