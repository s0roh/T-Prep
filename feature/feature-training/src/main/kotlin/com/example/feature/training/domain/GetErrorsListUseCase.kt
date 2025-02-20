package com.example.feature.training.domain

import com.example.training.domain.entity.TrainingError
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class GetErrorsListUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(trainingSessionId: String): List<TrainingError> =
        repository.getErrorsList(trainingSessionId)
}