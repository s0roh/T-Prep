package com.example.localdecks.domain.usecase

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.data.mapper.toDTO
import com.example.localdecks.domain.entity.DeckRequest
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import javax.inject.Inject

class SyncUpdateDeckUseCase @Inject constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) {

    suspend operator fun invoke(metadata: SyncMetadataDBO) {
        authRequestWrapper.executeWithAuth { token ->
            val metadataInfo = database.deckDao.getDeckById(metadata.deckId)
            if (metadataInfo == null) {
                Log.w("SyncWorker", "Колода с ID ${metadata.deckId} не найдена")
                return@executeWithAuth
            }
            val deckRequest = DeckRequest(
                name = metadataInfo.name,
                isPublic = metadataInfo.isPublic
            )
            val response = apiService.updateDeck(
                deckId = metadataInfo.serverDeckId ?: "",
                deckRequestDto = deckRequest.toDTO(),
                authHeader = token
            )
            if (response.isSuccessful) {
                Log.d("SyncWorker", "Колода обновлена успешно: ${metadata.deckId}")
                database.syncMetadataDao.deleteDeckSyncMetadata(metadata.deckId)
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
                    database.historyDao.deleteHistoryForDeck(metadata.deckId)
                    database.deckDao.deleteDeck(metadataInfo)
                }
                Log.e(
                    "SyncWorker",
                    "Ошибка при обновлении колоды: ${response.errorBody()?.string()}"
                )
            }
        }
    }
}