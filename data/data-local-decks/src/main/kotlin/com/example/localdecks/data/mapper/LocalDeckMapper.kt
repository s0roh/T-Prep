package com.example.localdecks.data.mapper

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.database.models.CardDBO
import com.example.database.models.DeckDBO
import com.example.localdecks.domain.entity.CardRequest
import com.example.localdecks.domain.entity.DeckRequest
import com.example.network.dto.collection.CardRequestDto
import com.example.network.dto.collection.DeckRequestDto

internal fun DeckDBO.toEntity(cards: List<Card>): Deck = Deck(
    id = id,
    name = name,
    isPublic = isPublic,
    cards = cards
)

internal fun CardDBO.toEntity(): Card = Card(
    id = id,
    question = question,
    answer = answer
)

internal fun Deck.toDBO(serverDeckId: String?, userId: String): DeckDBO = DeckDBO(
    id = id,
    serverDeckId = serverDeckId,
    name = name,
    isPublic = isPublic,
    userId = userId
)

internal fun Card.toDBO(deckId: String, serverCardId: Int?): CardDBO = CardDBO(
    id = id,
    serverCardId = serverCardId,
    deckId = deckId,
    question = question,
    answer = answer
)

internal fun DeckRequest.toDTO() = DeckRequestDto(
    name = name,
    isPublic = isPublic
)

internal fun CardRequest.toDTO() = CardRequestDto(
    question = question,
    answer = answer
)
