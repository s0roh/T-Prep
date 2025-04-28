package com.example.feature.training.presentation.training

sealed class TrainingUiEvent {

    data class PlaySound(val isCorrect: Boolean) : TrainingUiEvent()

    data object VibrateIncorrectAnswer : TrainingUiEvent()

    data object PlayFinishSound : TrainingUiEvent()
}
