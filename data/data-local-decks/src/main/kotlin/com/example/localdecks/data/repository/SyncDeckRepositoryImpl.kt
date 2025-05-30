package com.example.localdecks.data.repository

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.data.mapper.toDTO
import com.example.localdecks.domain.entity.DeckRequest
import com.example.localdecks.domain.repository.SyncDeckRepository
import com.example.network.api.ApiService
import com.example.preferences.auth.util.AuthRequestWrapper
import javax.inject.Inject

class SyncDeckRepositoryImpl @Inject constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) : SyncDeckRepository {

    override suspend fun syncCreateDeck(metadata: SyncMetadataDBO) {
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

            val response = apiService.createDeck(
                deckRequestDto = deckRequest.toDTO(),
                authHeader = token
            )

            if (response.isSuccessful) {
                Log.d("SyncWorker", "Колода создана успешно: ${response.body()?.id}")
                database.deckDao.updateDeck(
                    metadataInfo.copy(serverDeckId = response.body()?.id)
                )
                database.syncMetadataDao.deleteDeckSyncMetadata(metadata.deckId)
            } else {
                Log.e("SyncWorker", "Ошибка при создании колоды: ${response.errorBody()?.string()}")
            }
        }
    }

    override suspend fun syncDeleteDeck(metadata: SyncMetadataDBO) {
        authRequestWrapper.executeWithAuth { token ->
            val metadataInfo = database.deckDao.getDeckById(metadata.deckId)
            if (metadataInfo == null) {
                Log.w("SyncWorker", "Колода с ID ${metadata.deckId} не найдена")
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
                //Код 404 - колоды не существует
                if (response.code() == 404) {
                    Log.w(
                        "SyncWorker",
                        "Колода с ID ${metadata.deckId} не существует на сервере. Удаление из базы данных."
                    )
                    database.syncMetadataDao.deleteDeckSyncMetadata(metadata.deckId)
                    database.deckDao.deleteDeck(metadataInfo)
                }
                Log.e("SyncWorker", "Ошибка при удалении колоды: ${response.errorBody()?.string()}")
            }
        }
    }

    override suspend fun syncUpdateDeck(metadata: SyncMetadataDBO) {
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
                //Код 404 - колоды не существует
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
                    "Ошибка при обновлении колоды: ${response.errorBody()?.string()}"
                )
            }
        }
    }
}