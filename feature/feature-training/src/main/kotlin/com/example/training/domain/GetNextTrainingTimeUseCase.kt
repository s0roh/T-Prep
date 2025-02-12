package com.example.training.domain

import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class GetNextTrainingTimeUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(trainingSessionId: String) =
        repository.getNextTrainingTime(trainingSessionId)
}