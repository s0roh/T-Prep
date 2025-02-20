package com.example.feature.training.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun getContainerColor(
    isAnswered: Boolean,
    selectedAnswer: Boolean?,
    buttonAnswer: Boolean,
    correctAnswer: Boolean
): Color {
    return when {
        isAnswered && buttonAnswer == correctAnswer -> MaterialTheme.colorScheme.secondaryContainer
        isAnswered && selectedAnswer == buttonAnswer -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.background
    }
}

@Composable
internal fun getBorderColor(
    isAnswered: Boolean,
    selectedAnswer: Boolean?,
    buttonAnswer: Boolean,
    correctAnswer: Boolean
): Color {
    return when {
        isAnswered && buttonAnswer == correctAnswer -> MaterialTheme.colorScheme.background
        isAnswered && selectedAnswer != null && selectedAnswer != correctAnswer -> MaterialTheme.colorScheme.background
        else -> MaterialTheme.colorScheme.outline
    }
}

@Composable
internal fun getAnswerColor(
    isAnswered: Boolean,
    buttonAnswer: String,
    correctAnswer: String,
    selectedAnswer: String?,
): Color {
    return when {
        isAnswered && buttonAnswer == correctAnswer -> MaterialTheme.colorScheme.secondaryContainer
        isAnswered && buttonAnswer == selectedAnswer -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.background
    }
}

@Composable
internal fun getBorderColor(
    isAnswered: Boolean,
    buttonAnswer: String,
    correctAnswer: String,
    selectedAnswer: String?,
): Color {
    return when {
        isAnswered && buttonAnswer == correctAnswer -> MaterialTheme.colorScheme.background
        isAnswered && buttonAnswer == selectedAnswer-> MaterialTheme.colorScheme.background
        else -> MaterialTheme.colorScheme.outline
    }
}