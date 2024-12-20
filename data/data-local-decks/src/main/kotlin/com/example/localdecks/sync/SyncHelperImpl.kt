package com.example.localdecks.sync

import com.example.database.TPrepDatabase
import com.example.database.models.EntityType
import com.example.database.models.SyncMetadataDBO
import com.example.database.models.SyncStatus
import javax.inject.Inject

class SyncHelperImpl @Inject internal constructor(
    private val database: TPrepDatabase
) : SyncHelper {

    override suspend fun markAsNew(
        deckId: String,
        entityType: EntityType,
        cardId: Int?
    ) {
        when (entityType) {
            EntityType.DECK -> handleNewDeck(deckId)
            EntityType.CARD -> handleNewCard(deckId, cardId)
        }
    }

    override suspend fun markAsUpdated(
        deckId: String,
        entityType: EntityType,
        cardId: Int?
    ) {
        when (entityType) {
            EntityType.DECK -> handleUpdatedDeck(deckId)
            EntityType.CARD -> handleUpdatedCard(deckId, cardId)
        }
    }

    override suspend fun markAsDeleted(
        deckId: String,
        entityType: EntityType,
        cardId: Int?
    ) {
        when (entityType) {
            EntityType.DECK -> handleDeletedDeck(deckId)
            EntityType.CARD -> handleDeletedCard(deckId, cardId)
        }
    }

    private suspend fun handleNewDeck(deckId: String) {
        database.syncMetadataDao.insert(
            SyncMetadataDBO(
                id = 0,
                deckId = deckId,
                cardId = null,
                entityType = EntityType.DECK,
                status = SyncStatus.NEW,
                lastSynced = System.currentTimeMillis()
            )
        )
    }

    private suspend fun handleNewCard(deckId: String, cardId: Int?) {
        requireNotNull(cardId) { "cardId cannot be null for CARD entity type" }
        database.syncMetadataDao.insert(
            SyncMetadataDBO(
                id = 0,
                deckId = deckId,
                cardId = cardId,
                entityType = EntityType.CARD,
                status = SyncStatus.NEW,
                lastSynced = System.currentTimeMillis()
            )
        )
    }

    private suspend fun handleUpdatedDeck(deckId: String) {
        database.syncMetadataDao.deleteUpdatedDeckSyncMetadata(deckId)
        database.syncMetadataDao.insert(
            SyncMetadataDBO(
                id = 0,
                deckId = deckId,
                cardId = null,
                entityType = EntityType.DECK,
                status = SyncStatus.UPDATED,
                lastSynced = System.currentTimeMillis()
            )
        )
    }

    private suspend fun handleUpdatedCard(deckId: String, cardId: Int?) {
        requireNotNull(cardId) { "cardId cannot be null for CARD entity type" }
        database.syncMetadataDao.deleteUpdatedCardSyncMetadata(deckId, cardId)
        database.syncMetadataDao.insert(
            SyncMetadataDBO(
                id = 0,
                deckId = deckId,
                cardId = cardId,
                entityType = EntityType.CARD,
                status = SyncStatus.UPDATED,
                lastSynced = System.currentTimeMillis()
            )
        )
    }

    private suspend fun handleDeletedDeck(deckId: String) {
        val existingMetadata = database.syncMetadataDao.getDeckSyncMetadata(deckId)
        if (existingMetadata.any { it.status == SyncStatus.NEW }) {
            database.syncMetadataDao.deleteDeckSyncMetadata(deckId)
        } else {
            database.syncMetadataDao.deleteDeckSyncMetadata(deckId)

            database.syncMetadataDao.insert(
                SyncMetadataDBO(
                    id = 0,
                    deckId = deckId,
                    cardId = null,
                    entityType = EntityType.DECK,
                    status = SyncStatus.DELETED,
                    lastSynced = System.currentTimeMillis()
                )
            )
        }
    }

    private suspend fun handleDeletedCard(deckId: String, cardId: Int?) {
        requireNotNull(cardId) { "cardId cannot be null for CARD entity type" }
        val existingMetadata = database.syncMetadataDao.getCardSyncMetadata(deckId, cardId)
        if (existingMetadata.any { it.status == SyncStatus.NEW }) {
            database.syncMetadataDao.deleteCardSyncMetadata(deckId, cardId)
        } else {
            database.syncMetadataDao.deleteCardSyncMetadata(deckId, cardId)

            database.syncMetadataDao.insert(
                SyncMetadataDBO(
                    id = 0,
                    deckId = deckId,
                    cardId = cardId,
                    entityType = EntityType.CARD,
                    status = SyncStatus.DELETED,
                    lastSynced = System.currentTimeMillis()
                )
            )
        }
    }
}