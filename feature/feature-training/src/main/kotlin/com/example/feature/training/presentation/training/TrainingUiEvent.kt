package com.example.feature.training.presentation.training

sealed class TrainingUiEvent{

    data class PlaySound(val isCorrect: Boolean) : TrainingUiEvent()

    object VibrateIncorrectAnswer : TrainingUiEvent()

    object PlayFinishSound : TrainingUiEvent()
}
