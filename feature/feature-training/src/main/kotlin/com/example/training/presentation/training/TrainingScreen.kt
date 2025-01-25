package com.example.training.presentation.training

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.database.models.Source
import com.example.training.R
import com.example.training.presentation.components.FinishTrainingScreen
import com.example.training.presentation.components.QuestionArea

@Composable
fun TrainingScreen(
    paddingValues: PaddingValues,
    deckId: String,
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

        TrainingScreenState.Loading -> LoadingState()

        TrainingScreenState.Initial -> {}

    }
}

@Composable
private fun TrainingCardsContent(
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

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = "Тренировка",
                shouldShowArrowBack = true,
                onBackClick = onExit
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    //Spacer(modifier = Modifier.height(20.dp))
                    QuestionArea(question = currentCard.question)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                items(shuffledAnswers.size) { index ->
                    val answer = shuffledAnswers[index]
                    val color = getAnswerColor(isAnswered, answer, currentCard.answer, selectedAnswer)

                    AnswerButton(
                        answer = answer,
                        color = color,
                        isEnabled = !isAnswered,
                        onClick = {
                            if (!isAnswered) {
                                selectedAnswer = answer
                                isAnswered = true
                                showNextButton = true
                                onAnswer(answer == currentCard.answer, answer)
                            }
                        }
                    )
                }
            }

            Button(
                onClick = {
                    if (showNextButton) {
                        selectedAnswer = null
                        isAnswered = false
                        showNextButton = false
                        onNextCard()
                    } else {
                        onSkip()
                        isAnswered = true
                        showNextButton = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end= 16.dp, bottom = 20.dp)
            ) {
                Text(text = stringResource(if (showNextButton) R.string.next else R.string.skip))
            }
        }
    }
}


//@Composable
//private fun TrainingCardsContent(
//    currentState: TrainingScreenState.Success,
//    onAnswer: (Boolean, String?) -> Unit,
//    onSkip: () -> Unit,
//    onExit: () -> Unit,
//    onNextCard: () -> Unit
//) {
//    val currentCard = currentState.cards[currentState.currentCardIndex]
//    var selectedAnswer by remember(currentState.selectedAnswer) {
//        mutableStateOf(currentState.selectedAnswer)
//    }
//    var isAnswered by remember { mutableStateOf(currentState.selectedAnswer != null) }
//    var showNextButton by remember { mutableStateOf(currentState.selectedAnswer != null) }
//
//    val shuffledAnswers = remember(currentCard.id) {
//        (listOf(currentCard.answer) + currentCard.wrongAnswers).shuffled()
//    }
//
//    BackHandler(onBack = onExit)
//
//    Scaffold(
//        topBar = {
//            CenteredTopAppBar(
//                title = "Тренировка",
//                shouldShowArrowBack = true,
//                onBackClick = onExit
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//        ) {
//            Spacer(modifier = Modifier.height(20.dp))
//
//            QuestionArea(question = currentCard.question)
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            AnswerOptions(
//                shuffledAnswers = shuffledAnswers,
//                selectedAnswer = selectedAnswer,
//                isAnswered = isAnswered,
//                correctAnswer = currentCard.answer,
//                onAnswerSelected = {
//                    if (!isAnswered) {
//                        selectedAnswer = it
//                        isAnswered = true
//                        showNextButton = true
//                        onAnswer(it == currentCard.answer, it)
//                    }
//                }
//            )
//
//            Spacer(modifier = Modifier.weight(2f))
//
//            Button(
//                onClick = {
//                    if (showNextButton) {
//                        selectedAnswer = null
//                        isAnswered = false
//                        showNextButton = false
//                        onNextCard()
//                    } else {
//                        onSkip()
//                        isAnswered = true
//                        showNextButton = true
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 20.dp)
//            ) {
//                Text(text = stringResource(if (showNextButton) R.string.next else R.string.skip))
//            }
//        }
//    }
//}


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
            val color = getAnswerColor(isAnswered, answer, correctAnswer, selectedAnswer)

            AnswerButton(
                answer = answer,
                color = color,
                isEnabled = !isAnswered,
                onClick = { onAnswerSelected(answer) }
            )
        }
    }
}

@Composable
private fun AnswerButton(answer: String, color: Color, isEnabled: Boolean, onClick: () -> Unit) {
    val containerColor =
        if (!isEnabled && (color == Color.Green || color == MaterialTheme.colorScheme.error)) {
            color.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.background
        }

    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(disabledContainerColor = containerColor)
    ) {
        Text(
            text = answer,
//            maxLines = 4,
//            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun getAnswerColor(
    isAnswered: Boolean,
    answer: String,
    correctAnswer: String,
    selectedAnswer: String?
): Color {
    return when {
        isAnswered && answer == correctAnswer -> Color.Green
        isAnswered && answer == selectedAnswer -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onBackground
    }
}