package com.example.localdecks.domain.usecase

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.SyncMetadataDBO
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import javax.inject.Inject

class SyncDeleteCardUseCase @Inject constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) {

    suspend operator fun invoke(metadata: SyncMetadataDBO) {
        authRequestWrapper.executeWithAuth { token ->
            val metadataInfo = database.cardDao.getCardById(metadata.cardId ?: -1)
            val serverDeckId =
                database.deckDao.getDeckById(deckId = metadata.deckId)?.serverDeckId ?: ""
            if (metadataInfo == null) {
                Log.w("SyncWorker", "Карта с ID ${metadata.cardId} не найдена")
                return@executeWithAuth
            }
            val serverCardId = metadataInfo.serverCardId ?: -1

            val response = apiService.deleteCard(
                deckId = serverDeckId,
                cardId = serverCardId,
                authHeader = token
            )
            if (response.isSuccessful) {
                Log.d("SyncWorker", "Карта удалена успешно: ${response.body()?.message}")
                metadata.cardId?.let { cardId ->
                    database.syncMetadataDao.deleteCardSyncMetadata(
                        metadata.deckId,
                        cardId
                    )
                }
                database.cardDao.deleteCard(metadataInfo)
            } else {

                /**
                 * Код 404 - колоды не существует
                 *
                 * Если при изменении карточки получается что ее нет на сервере, значит она была
                 * удалена и необходимо ее удалить с устройства
                 */
                if (response.code() == 404) {
                    Log.w(
                        "SyncWorker",
                        "Карточка с ID ${metadata.cardId} не существует на сервере. Удаление из базы данных."
                    )

                    database.syncMetadataDao.deleteCardSyncMetadata(
                        metadataInfo.deckId,
                        metadataInfo.id
                    )
                    database.cardDao.deleteCard(metadataInfo)
                }
                Log.e(
                    "SyncWorker",
                    "Ошибка при удалении карты: ${response.errorBody()?.string()}"
                )
            }
        }
    }
}