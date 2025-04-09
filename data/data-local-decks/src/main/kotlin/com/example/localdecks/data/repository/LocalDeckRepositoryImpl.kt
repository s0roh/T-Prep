package com.example.localdecks.data.repository

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import com.example.database.TPrepDatabase
import com.example.database.models.EntityType
import com.example.localdecks.data.mapper.toDBO
import com.example.localdecks.data.mapper.toEntity
import com.example.localdecks.data.mapper.toUiModel
import com.example.localdecks.domain.repository.LocalDeckRepository
import com.example.localdecks.domain.repository.SyncHelper
import com.example.preferences.AuthPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDeckRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase,
    private val syncHelper: SyncHelper,
    private val preferences: AuthPreferences,
) : LocalDeckRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDecks(): Flow<List<DeckUiModel>> {
        return database.deckDao.getDecks().flatMapLatest { dboList ->

            if (dboList.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            combine(
                dboList.map { dbo ->
                    database.cardDao.getCardsForDeck(dbo.id).map { cardDboList ->
                        dbo.toUiModel(cardDboList.size)
                    }
                }
            ) { decks ->
                decks.toList()
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
        val userId = preferences.getUserId()
            ?: throw IllegalStateException("User ID not found in preferences")
        val dbo = deck.toDBO(serverDeckId = null, userId = userId)
        val generatedId = database.deckDao.insertDeck(dbo)

        syncHelper.markAsNew(deckId = generatedId, entityType = EntityType.DECK, cardId = null)
    }

    override suspend fun updateDeck(deck: Deck) {
        val userId = preferences.getUserId()
            ?: throw IllegalStateException("User ID not found in preferences")
        val existingDeck = database.deckDao.getDeckById(deck.id)
        if (existingDeck != null) {
            val updatedDeck = deck.toDBO(
                serverDeckId = existingDeck.serverDeckId,
                userId = userId
            )
            database.deckDao.updateDeck(updatedDeck)

            syncHelper.markAsUpdated(deckId = deck.id, entityType = EntityType.DECK, cardId = null)
        }
    }

    override suspend fun deleteDeck(deckId: String) {
        val existingDeck = database.deckDao.getDeckById(deckId)
        if (existingDeck != null) {
            val cards = database.cardDao.getCardsForDeckAndDeleted(deckId).firstOrNull() ?: emptyList()
            cards.forEach { card ->
                if (card.serverCardId == null) {
                    database.cardDao.deleteCard(card)
                } else {
                    database.cardDao.updateCard(card.copy(isDeleted = true))
                }
                syncHelper.markAsDeleted(
                    deckId = deckId,
                    entityType = EntityType.CARD,
                    cardId = card.id
                )
            }
            database.trainingModesHistoryDao.deleteTrainingModes(deckId)
            database.errorDao.deleteErrorForDeck(deckId)
            database.historyDao.deleteHistoryForDeck(deckId)
            if (existingDeck.serverDeckId == null) {
                database.deckDao.deleteDeck(existingDeck)
            } else {
                database.deckDao.updateDeck(existingDeck.copy(isDeleted = true))
            }
            syncHelper.markAsDeleted(deckId = deckId, entityType = EntityType.DECK, cardId = null)
        }
    }

    override suspend fun softDeleteDeck(deckId: String) {
        val existingDeck = database.deckDao.getDeckById(deckId)
        if (existingDeck != null) {
            // Помечаем карты как удаленные
            val cards = database.cardDao.getCardsForDeck(deckId).firstOrNull() ?: emptyList()
            cards.forEach { card ->
                database.cardDao.updateCard(card.copy(isDeleted = true))
            }

            // Помечаем колоду как удаленную
            database.deckDao.updateDeck(existingDeck.copy(isDeleted = true))
        }
    }

    override suspend fun restoreDeck(deckId: String) {
        val existingDeck = database.deckDao.getDeckById(deckId)
        if (existingDeck != null) {
            // Восстанавливаем карты
            val cards = database.cardDao.getCardsForDeckAndDeleted(deckId).firstOrNull()
                ?: emptyList()
            cards.forEach { card ->
                database.cardDao.updateCard(card.copy(isDeleted = false))
            }

            // Восстанавливаем колоду
            database.deckDao.updateDeck(existingDeck.copy(isDeleted = false))
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
            if (existingCard.serverCardId == null) {
                database.cardDao.deleteCard(existingCard)
            } else {
                database.cardDao.updateCard(existingCard.copy(isDeleted = true))
            }
            syncHelper.markAsDeleted(
                deckId = existingCard.deckId,
                entityType = EntityType.CARD,
                cardId = card.id
            )
        }
    }
}