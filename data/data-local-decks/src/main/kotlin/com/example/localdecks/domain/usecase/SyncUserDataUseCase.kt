package com.example.localdecks.domain.usecase

import com.example.localdecks.domain.repository.SyncUserDataRepository
import javax.inject.Inject

class SyncUserDataUseCase @Inject constructor(
    private val syncUserDataRepository: SyncUserDataRepository,
) {

    suspend operator fun invoke() = syncUserDataRepository.syncUserData()
}