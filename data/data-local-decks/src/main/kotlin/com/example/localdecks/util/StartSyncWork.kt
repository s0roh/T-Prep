package com.example.localdecks.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.localdecks.sync.SyncWorker
import java.util.UUID

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