package com.example.localdecks.domain.usecase

import com.example.database.models.SyncMetadataDBO
import com.example.localdecks.domain.repository.SyncCardRepository
import javax.inject.Inject

class SyncCreateCardUseCase @Inject constructor(
    private val syncCardRepository: SyncCardRepository,
) {

    suspend operator fun invoke(metadata: SyncMetadataDBO) =
        syncCardRepository.syncCreateCard(metadata)
}
