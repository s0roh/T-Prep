package com.example.feature.training.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.training.R

@Composable
internal fun TrueFalseButtons(
    isAnswered: Boolean,
    selectedAnswer: Boolean?,
    correctAnswer: Boolean,
    shakeOffset: Animatable<Float, AnimationVector1D>,
    onAnswerSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TrueFalseButtonItem(
            text = "ЛОЖЬ",
            isAnswered = isAnswered,
            selectedAnswer = selectedAnswer,
            correctAnswer = correctAnswer,
            onClick = { onAnswerSelected(false) },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
        TrueFalseButtonItem(
            text = "ИСТИНА",
            isAnswered = isAnswered,
            selectedAnswer = selectedAnswer,
            correctAnswer = correctAnswer,
            onClick = { onAnswerSelected(true) },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
    }
}

@Composable
private fun TrueFalseButtonItem(
    text: String,
    isAnswered: Boolean,
    selectedAnswer: Boolean?,
    correctAnswer: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor =
        getContainerColor(isAnswered, selectedAnswer, text == "ИСТИНА", correctAnswer)
    val borderColor = getBorderColor(isAnswered, selectedAnswer, text == "ИСТИНА", correctAnswer)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Card(
            onClick = onClick,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(2.dp, borderColor),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (text == "ИСТИНА") R.drawable.ic_true
                        else R.drawable.ic_false
                    ),
                    contentDescription = null,
                    tint = if (text == "ИСТИНА") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    modifier = if (text == "ИСТИНА") Modifier.size(100.dp) else Modifier.size((70.dp))
                )
            }
        }
    }
}