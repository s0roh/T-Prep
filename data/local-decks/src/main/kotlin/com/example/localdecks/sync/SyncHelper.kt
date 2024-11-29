package com.example.localdecks.sync

import com.example.database.models.EntityType

interface SyncHelper {

    suspend fun markAsNew(deckId: Long, entityType: EntityType, cardId: Long? = null)

    suspend fun markAsUpdated(deckId: Long, entityType: EntityType, cardId: Long? = null)

    suspend fun markAsDeleted(deckId: Long, entityType: EntityType, cardId: Long? = null)
}