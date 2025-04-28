package com.example.localdecks.domain.repository

import com.example.database.models.SyncMetadataDBO

interface SyncCardRepository {

    /**
     * Синхронизирует создание новой карточки.
     *
     * @param metadata Метаданные синхронизации [SyncMetadataDBO], описывающие создаваемую карточку.
     */
    suspend fun syncCreateCard(metadata: SyncMetadataDBO)

    /**
     * Синхронизирует удаление карточки.
     *
     * @param metadata Метаданные синхронизации [SyncMetadataDBO], описывающие удаляемую карточку.
     */
    suspend fun syncDeleteCard(metadata: SyncMetadataDBO)

    /**
     * Синхронизирует обновление карточки на сервере.
     *
     * @param metadata Метаданные для синхронизации.
     */
    suspend fun syncUpdateCard(metadata: SyncMetadataDBO)
}