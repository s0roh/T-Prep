package com.example.feature.training.domain

import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class GetDeckNameTrainingSessionTimeSourceUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(trainingSessionId: String): Triple<String, Long, Source> =
        repository.getDeckNameAndTrainingSessionTime(trainingSessionId)
}