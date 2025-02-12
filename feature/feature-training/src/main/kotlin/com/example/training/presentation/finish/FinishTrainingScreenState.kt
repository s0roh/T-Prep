package com.example.training.presentation.finish

import com.example.training.domain.entity.TrainingError

interface FinishTrainingScreenState {

    data object Initial : FinishTrainingScreenState

    data object Loading : FinishTrainingScreenState

    data class Success(
        val deckName: String,
        val trainingSessionTime: Long,
        val totalAnswers: Int,
        val correctAnswers: Int,
        val incorrectPercentage: Int,
        val correctPercentage: Int,
        val nextTrainingTime: Long?,
        val errorsList: List<TrainingError>,
    ) : FinishTrainingScreenState

    data class Error(val message: String) : FinishTrainingScreenState
}