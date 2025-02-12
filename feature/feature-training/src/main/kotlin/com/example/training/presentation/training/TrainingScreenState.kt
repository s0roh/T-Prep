package com.example.training.presentation.training

import com.example.common.domain.entity.Card

sealed interface TrainingScreenState {

    data object Initial : TrainingScreenState

    data object Loading : TrainingScreenState

    data class Success(
        val cards: List<Card>,
        val currentCardIndex: Int = 0,
        val correctAnswers: Int = 0,
        val selectedAnswer: String? = null
    ) : TrainingScreenState

    data class Finished(
        val totalCardsCompleted: Int,
        val correctAnswers: Int,
        val trainingSessionId: String
    ) : TrainingScreenState

    data class Error(val message: String) : TrainingScreenState
}