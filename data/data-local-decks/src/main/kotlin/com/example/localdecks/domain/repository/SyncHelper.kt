package com.example.localdecks.domain.repository

import com.example.database.models.EntityType

interface SyncHelper {

    suspend fun markAsNew(deckId: String, entityType: EntityType, cardId: Int? = null)

    suspend fun markAsUpdated(deckId: String, entityType: EntityType, cardId: Int? = null)

    suspend fun markAsDeleted(deckId: String, entityType: EntityType, cardId: Int? = null)
}