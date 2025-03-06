package com.example.feature.profile.domain

import com.example.history.domain.repository.HistoryRepository
import javax.inject.Inject

class GetTrainingStatsUseCase @Inject constructor(
    private val repository: HistoryRepository,
) {

    suspend operator fun invoke(): Pair<Int, Int> = repository.getTrainingStats()
}