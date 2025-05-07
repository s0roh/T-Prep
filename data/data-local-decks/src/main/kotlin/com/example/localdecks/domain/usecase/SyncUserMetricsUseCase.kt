package com.example.localdecks.domain.usecase

import com.example.localdecks.domain.repository.SyncUserMetricsRepository
import javax.inject.Inject

class SyncUserMetricsUseCase @Inject constructor(
    private val repository: SyncUserMetricsRepository,
) {

    suspend operator fun invoke() = repository.syncUserMetrics()
}