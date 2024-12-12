package com.example.localdecks.data.repository

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.database.TPrepDatabase
import com.example.database.models.EntityType
import com.example.localdecks.data.mapper.toDBO
import com.example.localdecks.data.mapper.toEntity
import com.example.localdecks.domain.repository.LocalDeckRepository
import com.example.localdecks.sync.SyncHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDeckRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase,
    private val syncHelper: SyncHelper
) : LocalDeckRepository {

    override fun getDecks(): Flow<List<Deck>> {
        return database.deckDao.getDecks().map { dboList ->
            dboList.map { dbo ->
                val cards = database.cardDao.getCardsForDeck(dbo.id).firstOrNull()?.map {
                    it.toEntity()
                } ?: emptyList()
                dbo.toEntity(cards)
            }
        }
    }

    override suspend fun getDeckById(deckId: String): Deck? {
        val cards = database.cardDao.getCardsForDeck(deckId).firstOrNull()?.map {
            it.toEntity()
        } ?: emptyList()
        return database.deckDao.getDeckById(deckId)?.toEntity(cards)
    }

    override suspend fun insertDeck(deck: Deck) {
        val dbo = deck.toDBO(serverDeckId = null)
        val generatedId = database.deckDao.insertDeck(dbo)

        syncHelper.markAsNew(deckId = generatedId, entityType = EntityType.DECK, cardId = null)
    }

    override suspend fun updateDeck(deck: Deck) {
        val existingDeck = database.deckDao.getDeckById(deck.id)
        if (existingDeck != null) {
            val updatedDeck = deck.toDBO(
                serverDeckId = existingDeck.serverDeckId
            )
            database.deckDao.updateDeck(updatedDeck)

            syncHelper.markAsUpdated(deckId = deck.id, entityType = EntityType.DECK, cardId = null)
        }
    }

    override suspend fun deleteDeck(deck: Deck) {
        val existingDeck = database.deckDao.getDeckById(deck.id)
        if (existingDeck != null) {
            val cards = database.cardDao.getCardsForDeck(deck.id).firstOrNull() ?: emptyList()
            cards.forEach { card ->
                database.cardDao.deleteCard(card)

                syncHelper.markAsDeleted(deckId = deck.id,
                entityType = EntityType.CARD,
                cardId = card.id)
            }
            database.historyDao.deleteHistoryForDeck(deck.id)

            database.deckDao.deleteDeck(existingDeck)

            syncHelper.markAsDeleted(deckId = deck.id, entityType = EntityType.DECK, cardId = null)

        }
    }

    override fun getCardsForDeck(deckId: String): Flow<List<Card>> {
        return database.cardDao.getCardsForDeck(deckId).map { dboList ->
            dboList.map { it.toEntity() }
        }
    }

    override suspend fun getCardById(cardId: Int): Card? {
        return database.cardDao.getCardById(cardId)?.toEntity()
    }

    override suspend fun insertCard(card: Card, deckId: String) {
        val dbo = card.toDBO(deckId = deckId, serverCardId = null)
        val generatedId = database.cardDao.insertCard(dbo).toInt()

        syncHelper.markAsNew(deckId = deckId, entityType = EntityType.CARD, cardId = generatedId)
    }

    override suspend fun updateCard(card: Card) {
        val existingCard = database.cardDao.getCardById(card.id)
        if (existingCard != null) {
            val dbo = card.toDBO(
                deckId = existingCard.deckId,
                serverCardId = existingCard.serverCardId
            )
            database.cardDao.updateCard(dbo)

            syncHelper.markAsUpdated(
                deckId = existingCard.deckId,
                entityType = EntityType.CARD,
                cardId = card.id
            )
        }
    }

    override suspend fun deleteCard(card: Card) {
        val existingCard = database.cardDao.getCardById(card.id)
        if (existingCard != null) {
            database.cardDao.deleteCard(existingCard)

            syncHelper.markAsDeleted(
                deckId = existingCard.deckId,
                entityType = EntityType.CARD,
                cardId = card.id
            )
        }
    }
}