package com.example.localdecks.domain.repository

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    // DeckDao

    fun getDecks(): Flow<List<Deck>>

    suspend fun getDeckById(deckId: String): Deck?

    suspend fun insertDeck(deck: Deck)

    suspend fun updateDeck(deck: Deck)

    suspend fun deleteDeck(deck: Deck)

    // CardDao

    fun getCardsForDeck(deckId: String): Flow<List<Card>>

    suspend fun getCardById(cardId: Int): Card?

    suspend fun insertCard(card: Card, deckId: String)

    suspend fun updateCard(card: Card)

    suspend fun deleteCard(card: Card)
}