package com.example.localdecks.domain.usecase

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.SyncMetadataDBO
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import javax.inject.Inject

class SyncDeleteDeckUseCase @Inject constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) {

    suspend operator fun invoke(metadata: SyncMetadataDBO) {
        authRequestWrapper.executeWithAuth { token ->
            val metadataInfo = database.deckDao.getDeckById(metadata.deckId)
            if (metadataInfo == null) {
                Log.w("SyncWorker", "Карта с ID ${metadata.cardId} не найдена")
                return@executeWithAuth
            }
            val serverDeckId = metadataInfo.serverDeckId ?: ""
            val response = apiService.deleteDeck(
                deckId = serverDeckId,
                authHeader = token
            )
            if (response.isSuccessful) {
                Log.d("SyncWorker", "Колода удалена успешно: ${metadata.deckId}")
                database.syncMetadataDao.deleteDeckSyncMetadata(metadata.deckId)
                database.deckDao.deleteDeck(metadataInfo)
            } else {

                /**
                 * Код 404 - колоды не существует
                 *
                 * Если при изменении колоды получается что ее нет на сервере, значит она была
                 * удалена и необходимо ее удалить с устройства
                 */
                if (response.code() == 404) {
                    Log.w(
                        "SyncWorker",
                        "Колода с ID ${metadata.deckId} не существует на сервере. Удаление из базы данных."
                    )
                    database.syncMetadataDao.deleteDeckSyncMetadata(metadata.deckId)
                    database.deckDao.deleteDeck(metadataInfo)
                }
                Log.e(
                    "SyncWorker",
                    "Ошибка при удалении колоды: ${response.errorBody()?.string()}"
                )
            }
        }
    }
}