package com.example.feature.history.domain.usecase

import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.repository.HistoryRepository
import javax.inject.Inject

internal class GetTrainingHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {

    suspend operator fun invoke(): List<TrainingHistoryItem> = repository.getTrainingHistory()
}