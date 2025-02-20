package com.example.feature.training.domain

import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

class CheckFillInTheBlankAnswerUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(userInput: String, correctWords: List<String>): Boolean =
        repository.checkFillInTheBlankAnswer(userInput = userInput, correctWords = correctWords)
}