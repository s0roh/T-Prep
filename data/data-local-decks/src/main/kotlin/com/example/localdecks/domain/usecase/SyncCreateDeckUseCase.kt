package com.example.localdecks.domain.usecase

import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.domain.repository.SyncDeckRepository
import javax.inject.Inject

class SyncCreateDeckUseCase @Inject constructor(
    private val syncDeckRepository: SyncDeckRepository,
) {

    suspend operator fun invoke(metadata: SyncMetadataDBO) =
        syncDeckRepository.syncCreateDeck(metadata)
}