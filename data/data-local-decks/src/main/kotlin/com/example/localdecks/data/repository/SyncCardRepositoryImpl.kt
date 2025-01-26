package com.example.localdecks.data.repository

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.data.mapper.toDTO
import com.example.localdecks.domain.entity.CardRequest
import com.example.localdecks.domain.repository.SyncCardRepository
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import javax.inject.Inject

class SyncCardRepositoryImpl @Inject constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) : SyncCardRepository {

    override suspend fun syncCreateCard(metadata: SyncMetadataDBO) {
        authRequestWrapper.executeWithAuth { token ->
            val metadataInfo = database.cardDao.getCardById(metadata.cardId ?: -1)
            val serverDeckId =
                database.deckDao.getDeckById(deckId = metadata.deckId)?.serverDeckId ?: ""
            if (metadataInfo == null) {
                Log.w("SyncWorker", "Карта с ID ${metadata.cardId} не найдена")
                return@executeWithAuth
            }

            val cardRequest = CardRequest(
                question = metadataInfo.question,
                answer = metadataInfo.answer
            )

            val response = apiService.createCard(
                deckId = serverDeckId,
                cardRequestDto = cardRequest.toDTO(),
                authHeader = token
            )

            if (response.isSuccessful) {
                Log.d("SyncWorker", "Карта создана успешно: ${response.body()?.id}")
                database.cardDao.updateCard(metadataInfo.copy(serverCardId = response.body()?.id))
                metadata.cardId?.let { cardId ->
                    database.syncMetadataDao.deleteCardSyncMetadata(metadata.deckId, cardId)
                }
            } else {
                Log.e("SyncWorker", "Ошибка при создании карты: ${response.errorBody()?.string()}")
            }
        }
    }

    override suspend fun syncDeleteCard(metadata: SyncMetadataDBO) {
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
                    database.syncMetadataDao.deleteCardSyncMetadata(metadata.deckId, cardId)
                }
                database.cardDao.deleteCard(metadataInfo)
            } else {
                //Код 404 - карточки не существует
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
                Log.e("SyncWorker", "Ошибка при удалении карты: ${response.errorBody()?.string()}")
            }
        }
    }

    override suspend fun syncUpdateCard(metadata: SyncMetadataDBO) {
        authRequestWrapper.executeWithAuth { token ->
            val metadataInfo = database.cardDao.getCardById(metadata.cardId ?: -1)
            val serverDeckId =
                database.deckDao.getDeckById(deckId = metadata.deckId)?.serverDeckId ?: ""
            if (metadataInfo == null) {
                Log.w("SyncWorker", "Карта с ID ${metadata.cardId} не найдена")
                return@executeWithAuth
            }

            val serverCardId = metadataInfo.serverCardId ?: -1
            val cardRequest = CardRequest(
                question = metadataInfo.question,
                answer = metadataInfo.answer
            )

            val response = apiService.updateCard(
                deckId = serverDeckId,
                cardId = serverCardId,
                cardRequestDto = cardRequest.toDTO(),
                authHeader = token
            )

            if (response.isSuccessful) {
                Log.d("SyncWorker", "Карта обновлена успешно: ${response.body()?.message}")
                metadata.cardId?.let { cardId ->
                    database.syncMetadataDao.deleteCardSyncMetadata(metadata.deckId, cardId)
                }
            } else {
                //Код 404 - карточки не существует
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
                    "Ошибка при обновлении карты: ${response.errorBody()?.string()}"
                )
            }
        }
    }
}