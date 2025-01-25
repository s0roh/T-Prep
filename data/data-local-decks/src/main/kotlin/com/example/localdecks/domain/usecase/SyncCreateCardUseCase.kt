package com.example.localdecks.domain.usecase

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.data.mapper.toDTO
import com.example.localdecks.domain.entity.CardRequest
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import javax.inject.Inject

class SyncCreateCardUseCase @Inject constructor(
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
                    database.syncMetadataDao.deleteCardSyncMetadata(
                        metadata.deckId,
                        cardId
                    )
                }
            } else {
                Log.e(
                    "SyncWorker",
                    "Ошибка при создании карты: ${response.errorBody()?.string()}"
                )
            }
        }
    }
}
