package com.example.feature.training.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun MultipleChoiceAnswerList(
    answers: List<String>,
    isAnswered: Boolean,
    selectedAnswer: String?,
    correctAnswer: String,
    shakeOffset: Animatable<Float, AnimationVector1D>,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        answers.forEach { answer ->
            MultipleChoiceButton(
                modifier = Modifier.offset(x = shakeOffset.value.dp),
                answer = answer,
                containerColor = getAnswerColor(isAnswered, answer, correctAnswer, selectedAnswer),
                borderColor = getBorderColor(isAnswered, answer, correctAnswer, selectedAnswer),
                isEnabled = !isAnswered,
                onClick = { onAnswerSelected(answer) }
            )
        }
    }
}

@Composable
private fun MultipleChoiceButton(
    answer: String,
    containerColor: Color,
    borderColor: Color,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(disabledContainerColor = containerColor)
    ) {
        Text(
            text = answer,
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}