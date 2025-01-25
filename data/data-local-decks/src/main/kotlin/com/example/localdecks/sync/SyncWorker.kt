package com.example.localdecks.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.database.models.EntityType
import com.example.database.models.SyncMetadataDBO
import com.example.database.models.SyncStatus
import com.example.localdecks.domain.usecase.GetSyncMetadataList
import com.example.localdecks.domain.usecase.SyncCreateCardUseCase
import com.example.localdecks.domain.usecase.SyncCreateDeckUseCase
import com.example.localdecks.domain.usecase.SyncDeleteCardUseCase
import com.example.localdecks.domain.usecase.SyncDeleteDeckUseCase
import com.example.localdecks.domain.usecase.SyncUpdateCardUseCase
import com.example.localdecks.domain.usecase.SyncUpdateDeckUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getSyncMetadataList: GetSyncMetadataList,
    private val syncCreateDeckUseCase: SyncCreateDeckUseCase,
    private val syncUpdateDeckUseCase: SyncUpdateDeckUseCase,
    private val syncDeleteDeckUseCase: SyncDeleteDeckUseCase,
    private val syncCreateCardUseCase: SyncCreateCardUseCase,
    private val syncUpdateCardUseCase: SyncUpdateCardUseCase,
    private val syncDeleteCardUseCase: SyncDeleteCardUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncWorker", "Запущена синхронизация данных")
            val syncMetadataList = getSyncMetadataList()
            syncMetadataList.forEach { metadata ->
                withContext(Dispatchers.IO) {
                    when (metadata.entityType) {
                        EntityType.DECK -> {
                            Log.d("SyncWorker", "Синхронизация колоды: ${metadata.deckId}")
                            syncDeck(metadata)
                        }

                        EntityType.CARD -> {
                            Log.d("SyncWorker", "Синхронизация карты: ${metadata.cardId}")
                            syncCard(metadata)
                        }
                    }
                }
            }
            Log.d("SyncWorker", "Синхронизация завершена успешно")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Ошибка при синхронизации", e)
            Result.retry()
        }
    }

    private suspend fun syncDeck(metadata: SyncMetadataDBO) {
        Log.d(
            "SyncWorker",
            "Начата синхронизация колоды: ${metadata.deckId}, статус: ${metadata.status}"
        )
        when (metadata.status) {
            SyncStatus.NEW -> {
                syncCreateDeckUseCase(metadata)
            }

            SyncStatus.UPDATED -> {
                syncUpdateDeckUseCase(metadata)
            }

            SyncStatus.DELETED -> {
                syncDeleteDeckUseCase(metadata)
            }
        }
    }

    private suspend fun syncCard(metadata: SyncMetadataDBO) {
        Log.d(
            "SyncWorker",
            "Начата синхронизация карты: ${metadata.cardId}, статус: ${metadata.status}"
        )
        when (metadata.status) {
            SyncStatus.NEW -> {
                syncCreateCardUseCase(metadata)
            }

            SyncStatus.UPDATED -> {
                syncUpdateCardUseCase(metadata)
            }

            SyncStatus.DELETED -> {
                syncDeleteCardUseCase(metadata)
            }
        }
    }
}