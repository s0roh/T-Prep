package com.example.feature.training.domain

import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class GetTotalAndCorrectCountAnswersUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(trainingSessionId: String): Pair<Int, Int> =
        repository.getTotalAndCorrectCountAnswers(trainingSessionId)
}