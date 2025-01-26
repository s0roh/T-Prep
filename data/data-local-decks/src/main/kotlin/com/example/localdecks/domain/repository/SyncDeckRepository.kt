package com.example.localdecks.domain.repository

import com.example.database.models.SyncMetadataDBO

interface SyncDeckRepository {

    suspend fun syncCreateDeck(metadata: SyncMetadataDBO)

    suspend fun syncDeleteDeck(metadata: SyncMetadataDBO)

    suspend fun syncUpdateDeck(metadata: SyncMetadataDBO)
}