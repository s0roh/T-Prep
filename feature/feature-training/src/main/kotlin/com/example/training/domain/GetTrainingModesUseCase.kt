package com.example.training.domain

import com.example.training.domain.entity.TrainingModes
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class GetTrainingModesUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(deckId: String): TrainingModes =
        repository.getTrainingModes(deckId = deckId)
}