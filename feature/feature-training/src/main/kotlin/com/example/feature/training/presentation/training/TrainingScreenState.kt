package com.example.feature.training.presentation.training

import android.net.Uri
import com.example.training.domain.entity.TrainingCard

sealed interface TrainingScreenState {

    data object Initial : TrainingScreenState

    data object Loading : TrainingScreenState

    data class Success(
        val cards: List<TrainingCard>,
        val currentCardIndex: Int = 0,
        val currentCardPictureUri: Uri? = null,
        val nextCardPictureUri: Uri? = null,
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