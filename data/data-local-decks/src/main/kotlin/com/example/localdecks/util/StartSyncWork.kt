package com.example.localdecks.util

import android.content.Context
import androidx.work.Constraints
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
 * @return UUID созданной задачи синхронизации.
 */
fun startSyncWork(context: Context): UUID {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val syncWorkRequest = OneTimeWorkRequest.Builder(SyncWorker::class.java)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "SyncWork",
        ExistingWorkPolicy.KEEP,
        syncWorkRequest
    )

    return syncWorkRequest.id
}