package com.example.feature.training.presentation.training_results

import com.example.training.domain.entity.TrainingError

interface TrainingResultsScreenState {

    data object Initial : TrainingResultsScreenState

    data object Loading : TrainingResultsScreenState

    data class Success(
        val deckName: String,
        val trainingSessionTime: Long,
        val totalAnswers: Int,
        val correctAnswers: Int,
        val incorrectPercentage: Int,
        val correctPercentage: Int,
        val nextTrainingTime: Long?,
        val errorsList: List<TrainingError>,
    ) : TrainingResultsScreenState

    data class Error(val message: String) : TrainingResultsScreenState
}