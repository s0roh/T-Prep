package com.example.localdecks.domain.repository

import com.example.database.models.SyncMetadataDBO

interface SyncDeckRepository {

    /**
     * Синхронизирует создание новой колоды.
     *
     * @param metadata Метаданные синхронизации [SyncMetadataDBO], описывающие создаваемую колоду.
     */
    suspend fun syncCreateDeck(metadata: SyncMetadataDBO)

    /**
     * Синхронизирует удаление колоды.
     *
     * @param metadata Метаданные синхронизации [SyncMetadataDBO], описывающие удаляемую колоду.
     */
    suspend fun syncDeleteDeck(metadata: SyncMetadataDBO)

    /**
     * Синхронизирует обновление колоды.
     *
     * @param metadata Метаданные синхронизации [SyncMetadataDBO], описывающие обновляемую колоду.
     */
    suspend fun syncUpdateDeck(metadata: SyncMetadataDBO)
}