package com.example.localdecks.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.localdecks.sync.SyncWorker
import java.util.UUID

/**
 * Запускает однократную задачу синхронизации данных через WorkManager.
 *
 * - Задача будет выполнена только при наличии сетевого подключения.
 * - Если задача с именем "SyncWork" уже запущена или запланирована, новая задача не будет создана (ExistingWorkPolicy.KEEP).
 *
 * @param context Контекст приложения.
 * @param shouldSyncMetrics Флаг, указывающий, нужно ли синхронизировать пользовательские метрики. По умолчанию равен `false`.
 * @return UUID созданной задачи синхронизации.
 */
fun startSyncWork(context: Context, shouldSyncMetrics: Boolean = false): UUID {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val inputData = Data.Builder()
        .putBoolean(SyncWorker.SHOULD_SYNC_USER_METRICS_KEY, shouldSyncMetrics)
        .build()

    val syncWorkRequest = OneTimeWorkRequest.Builder(SyncWorker::class.java)
        .setConstraints(constraints)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "SyncWork",
        ExistingWorkPolicy.KEEP,
        syncWorkRequest
    )

    return syncWorkRequest.id
}