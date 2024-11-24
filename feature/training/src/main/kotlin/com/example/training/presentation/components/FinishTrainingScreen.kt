package com.example.training.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.training.R

@Composable
internal fun FinishTrainingScreen(
    paddingValues: PaddingValues,
    totalCardsCompleted: Int,
    correctAnswers: Int,
    onFinishClick: () -> Unit
) {
    val correctPercentage =
        if (totalCardsCompleted > 0) correctAnswers.toFloat() / totalCardsCompleted else 0f
    val incorrectPercentage = 1f - correctPercentage

    var isClicked by remember { mutableStateOf(false) }

    BackHandler(onBack = onFinishClick)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.training_is_over),
            style = TextStyle(fontSize = 24.sp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutChart(
                modifier = Modifier.size(100.dp),
                correctPercentage = correctPercentage,
                incorrectPercentage = incorrectPercentage,
                correctColor = Color(0xFFA8E6A1),
                incorrectColor = Color(0xFFFFB3B3)
            )

            Spacer(modifier = Modifier.width(30.dp))

            Column {
                Text(
                    text = stringResource(R.string.total_cards_passed, totalCardsCompleted),
                    style = TextStyle(fontSize = 18.sp)
                )
                Text(
                    text = stringResource(R.string.correct_answers, correctAnswers),
                    style = TextStyle(fontSize = 18.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (!isClicked) {
                    onFinishClick()
                    isClicked = true
                }
            },
            enabled = !isClicked
        ) {
            Text(text = stringResource(R.string.close))
        }
    }
}

@Composable
internal fun DonutChart(
    modifier: Modifier = Modifier,
    correctPercentage: Float,
    incorrectPercentage: Float,
    correctColor: Color,
    incorrectColor: Color,
    strokeWidth: Float = 80f,
) {
    Canvas(modifier = modifier) {
        val correctSweepAngle = 360 * correctPercentage
        drawArc(
            color = correctColor,
            startAngle = -90f,
            sweepAngle = correctSweepAngle,
            useCenter = false,
            style = Stroke(strokeWidth),
            size = size
        )

        val incorrectStartAngle = -90f + correctSweepAngle
        drawArc(
            color = incorrectColor,
            startAngle = incorrectStartAngle,
            sweepAngle = 360 * incorrectPercentage,
            useCenter = false,
            style = Stroke(strokeWidth),
            size = size
        )
    }
}