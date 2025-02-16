package com.example.training.presentation.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import com.example.database.models.TrainingMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun handleAnswerSelection(
    selected: String,
    correct: String,
    onAnswer: (Boolean, String?, TrainingMode) -> Unit,
    shakeOffset: Animatable<Float, AnimationVector1D>,
    coroutineScope: CoroutineScope,
    onSelected: (String) -> Unit
) {
    onSelected(selected)
    val isCorrect = selected == correct
    onAnswer(isCorrect, selected, TrainingMode.MULTIPLE_CHOICE)
    if (!isCorrect) coroutineScope.launch { launchShakeAnimation(shakeOffset) }
}