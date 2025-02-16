package com.example.training.domain

import com.example.training.domain.entity.TrainingModes
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class SaveTrainingModesUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(trainingModes: TrainingModes) =
        repository.saveTrainingModes(trainingModes = trainingModes)
}