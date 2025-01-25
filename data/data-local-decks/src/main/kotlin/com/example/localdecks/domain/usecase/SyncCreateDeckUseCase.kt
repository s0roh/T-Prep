package com.example.localdecks.domain.usecase

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.data.mapper.toDTO
import com.example.localdecks.domain.entity.DeckRequest
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import javax.inject.Inject

class SyncCreateDeckUseCase @Inject constructor(
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
                Log.e(
                    "SyncWorker",
                    "Ошибка при создании колоды: ${response.errorBody()?.string()}"
                )
            }
        }
    }
}