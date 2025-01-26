package com.example.localdecks.domain.usecase

import com.example.database.TPrepDatabase
import javax.inject.Inject

class GetSyncMetadataList @Inject constructor(
    private val database: TPrepDatabase
) {

    suspend operator fun invoke() = database.syncMetadataDao.getSyncItems()
}