package com.example.training.domain

import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class GetInfoForNavigationToDeckUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(trainingSessionId: String): Pair<String, Source> =
        repository.getInfoForNavigationToDeck(trainingSessionId = trainingSessionId)
}