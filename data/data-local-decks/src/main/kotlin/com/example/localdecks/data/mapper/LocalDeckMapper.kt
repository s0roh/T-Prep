package com.example.localdecks.data.mapper

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.database.models.CardDBO
import com.example.database.models.DeckDBO

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

internal fun Deck.toDBO(serverDeckId: Long?): DeckDBO = DeckDBO(
    id = id,
    serverDeckId = serverDeckId,
    name = name,
    isPublic = isPublic
)

internal fun Card.toDBO(deckId: Long, serverCardId: Long?): CardDBO = CardDBO(
    id = id,
    serverCardId = serverCardId,
    deckId = deckId,
    question = question,
    answer = answer
)
