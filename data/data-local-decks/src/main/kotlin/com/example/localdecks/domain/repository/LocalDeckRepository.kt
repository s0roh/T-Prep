package com.example.localdecks.domain.repository

import android.net.Uri
import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    // DeckDao

    fun getDecks(): Flow<List<DeckUiModel>>

    suspend fun getDeckById(deckId: String): Deck?

    suspend fun insertDeck(deck: Deck)

    suspend fun updateDeck(deck: Deck)

    suspend fun deleteDeck(deckId: String)

    suspend fun softDeleteDeck(deckId: String)

    suspend fun restoreDeck(deckId: String)

    // CardDao

    fun getCardsForDeck(deckId: String): Flow<List<Card>>

    suspend fun getCardById(cardId: Int): Card?

    suspend fun getCardPicture(deckId: String, cardId: Int): Uri?

    suspend fun insertCard(card: Card, deckId: String): Int

    suspend fun updateCard(card: Card)

    suspend fun updateCardPicture(deckId: String, cardId: Int, pictureUri: Uri)

    suspend fun deleteCard(card: Card)

    suspend fun deleteCardPicture(deckId: String, cardId: Int)
}