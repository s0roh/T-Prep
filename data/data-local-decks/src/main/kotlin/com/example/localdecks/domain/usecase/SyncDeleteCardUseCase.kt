package com.example.localdecks.domain.usecase

import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.domain.repository.SyncCardRepository
import javax.inject.Inject

class SyncDeleteCardUseCase @Inject constructor(
    private val syncCardRepository: SyncCardRepository,
) {

    suspend operator fun invoke(metadata: SyncMetadataDBO) =
        syncCardRepository.syncDeleteCard(metadata)
}