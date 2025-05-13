package com.example.localdecks.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
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
import com.example.preferences.auth.AuthPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class LocalDeckRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
            val cards =
                database.cardDao.getCardsForDeckAndDeleted(deckId).firstOrNull() ?: emptyList()
            cards.forEach { card ->
                if (card.serverCardId == null) {
                    database.cardDao.deleteCard(card)
                } else {
                    database.cardDao.updateCard(card.copy(isDeleted = true))
                }
            }
            database.trainingModesHistoryDao.deleteTrainingModes(deckId)
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

    override suspend fun getCardPicture(
        deckId: String,
        cardId: Int,
    ): Uri? {
        return try {
            val card = database.cardDao.getCardById(cardId)
            val path = card?.picturePath ?: return null
            File(path).toUri()
        } catch (e: Exception) {
            Log.e("LocalDeckRepositoryImpl", "getCardPicture: ${e.message}")
            null
        }
    }

    override suspend fun insertCard(card: Card, deckId: String): Int {
        val dbo = card.toDBO(deckId = deckId, serverCardId = null)
        val generatedId = database.cardDao.insertCard(dbo).toInt()

        syncHelper.markAsNew(deckId = deckId, entityType = EntityType.CARD, cardId = generatedId)

        return generatedId
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

    override suspend fun updateCardPicture(
        deckId: String,
        cardId: Int,
        pictureUri: Uri,
    ) {
        val existingCard = database.cardDao.getCardById(cardId) ?: return
        val file = File(context.filesDir, "card_${cardId}_$deckId.jpg")

        val inputStream = context.contentResolver.openInputStream(pictureUri)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        if (file.length() > 5 * 1024 * 1024) {
            Log.e(
                "LocalDeckRepositoryImpl",
                "updateUserProfileImage: Image size exceeds 5MB limit"
            )
            file.delete()
            throw Exception("Image size exceeds 5MB limit")
        }

        database.cardDao.updateCard(existingCard.copy(picturePath = file.absolutePath))
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

    override suspend fun deleteCardPicture(
        deckId: String,
        cardId: Int,
    ) {
        val existingCard = database.cardDao.getCardById(cardId) ?: return
        existingCard.picturePath?.let { path ->
            val file = File(path)
            if (file.exists()) file.delete()
        }

        database.cardDao.updateCard(existingCard.copy(picturePath = null))
    }
}