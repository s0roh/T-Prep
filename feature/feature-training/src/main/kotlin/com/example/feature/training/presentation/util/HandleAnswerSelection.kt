package com.example.feature.training.presentation.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import com.example.database.models.TrainingMode
import com.example.training.domain.entity.TrainingCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun handleAnswerSelection(
    card: TrainingCard,
    selected: String,
    onAnswer: (Boolean, String, String, String?, TrainingMode) -> Unit,
    shakeOffset: Animatable<Float, AnimationVector1D>,
    coroutineScope: CoroutineScope,
    onSelected: (String) -> Unit
) {
    onSelected(selected)
    val isCorrect = selected == card.answer
    onAnswer(isCorrect, card.question, card.answer, selected, TrainingMode.MULTIPLE_CHOICE)
    if (!isCorrect) coroutineScope.launch { launchShakeAnimation(shakeOffset) }
}