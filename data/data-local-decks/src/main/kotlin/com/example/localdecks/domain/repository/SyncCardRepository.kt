package com.example.localdecks.domain.repository

import com.example.database.models.SyncMetadataDBO

interface SyncCardRepository {

    suspend fun syncCreateCard(metadata: SyncMetadataDBO)

    suspend fun syncDeleteCard(metadata: SyncMetadataDBO)

    suspend fun syncUpdateCard(metadata: SyncMetadataDBO)
}