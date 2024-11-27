package com.example.training.presentation.training

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.database.models.Source
import com.example.training.R
import com.example.training.presentation.components.FinishTrainingScreen

@Composable
fun TrainingScreen(
    paddingValues: PaddingValues,
    deckId: Long,
    source: Source,
    onFinishClick: () -> Unit
) {
    val viewModel: TrainingViewModel = hiltViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(key1 = deckId, key2 = source) {
        if (screenState.value is TrainingScreenState.Initial) {
            viewModel.loadTraining(deckId, source)
        }
    }

    when (val currentState = screenState.value) {

        is TrainingScreenState.Success -> {
            TrainingCardsContent(
                paddingValues = paddingValues,
                currentState = currentState,
                onAnswer = { isCorrect, answer -> viewModel.recordAnswer(isCorrect, answer) },
                onSkip = { viewModel.recordAnswer(false) },
                onExit = { viewModel.exitTraining() },
                onNextCard = { viewModel.moveToNextCardOrFinish() }
            )
        }

        is TrainingScreenState.Finished -> {
            FinishTrainingScreen(
                paddingValues = paddingValues,
                totalCardsCompleted = currentState.totalCardsCompleted,
                correctAnswers = currentState.correctAnswers,
                onFinishClick = onFinishClick
            )
        }

        is TrainingScreenState.Error -> ErrorState(message = currentState.message)

        TrainingScreenState.Initial -> {}

        TrainingScreenState.Loading -> LoadingState()


    }

}

@Composable
private fun TrainingCardsContent(
    paddingValues: PaddingValues,
    currentState: TrainingScreenState.Success,
    onAnswer: (Boolean, String?) -> Unit,
    onSkip: () -> Unit,
    onExit: () -> Unit,
    onNextCard: () -> Unit
) {
    val currentCard = currentState.cards[currentState.currentCardIndex]
    var selectedAnswer by remember(currentState.selectedAnswer) {
        mutableStateOf(currentState.selectedAnswer)
    }
    var isAnswered by remember { mutableStateOf(currentState.selectedAnswer != null) }
    var showNextButton by remember { mutableStateOf(currentState.selectedAnswer != null) }

    val shuffledAnswers = remember(currentCard.id) {
        (listOf(currentCard.answer) + currentCard.wrongAnswers).shuffled()
    }

    BackHandler(onBack = onExit)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBarWithBackIcon(onBackClick = onExit)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Вопрос: ${currentCard.question}",
            style = TextStyle(fontSize = 20.sp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        AnswerOptions(
            shuffledAnswers = shuffledAnswers,
            selectedAnswer = selectedAnswer,
            isAnswered = isAnswered,
            correctAnswer = currentCard.answer,
            onAnswerSelected = {
                if (!isAnswered) {
                    selectedAnswer = it
                    isAnswered = true
                    showNextButton = true
                    onAnswer(it == currentCard.answer, it)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (showNextButton) {
            Button(
                onClick = {
                    if (isAnswered) {
                        selectedAnswer = null
                        isAnswered = false
                        showNextButton = false
                        onNextCard()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.next))
            }
        } else {
            Button(
                onClick = {
                    onSkip()
                    isAnswered = true
                    showNextButton = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.skip))
            }
        }
    }
}


@Composable
private fun TopBarWithBackIcon(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AnswerOptions(
    shuffledAnswers: List<String>,
    selectedAnswer: String?,
    isAnswered: Boolean,
    correctAnswer: String,
    onAnswerSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        shuffledAnswers.forEach { answer ->
            val colorOfAnswer = when {
                isAnswered && answer == correctAnswer -> Color.Green
                isAnswered && answer == selectedAnswer -> MaterialTheme.colorScheme.error
                isAnswered -> Color.Gray
                else -> Color.Gray
            }
            AnswerButton(
                answer = answer,
                color = colorOfAnswer,
                isEnabled = !isAnswered,
                onClick = { onAnswerSelected(answer) }
            )
        }
    }
}


@Composable
private fun AnswerButton(answer: String, color: Color, isEnabled: Boolean, onClick: () -> Unit) {
    Card(
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(CircleShape)
            .clickable(enabled = isEnabled, onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(2.dp, color)
    ) {
        Text(
            text = answer,
            modifier = Modifier.padding(16.dp),
            color = color
        )
    }
}