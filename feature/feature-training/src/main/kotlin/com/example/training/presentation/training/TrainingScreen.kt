package com.example.training.presentation.training

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.domain.entity.Card
import com.example.common.domain.entity.TrainingMode
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.common.ui.NavigationIconType
import com.example.database.models.Source
import com.example.training.presentation.components.NextOrSkipButton
import com.example.training.presentation.components.QuestionArea
import com.example.training.presentation.components.getAnswerColor
import com.example.training.presentation.components.getContainerColor
import com.example.training.R
import com.example.training.presentation.components.getBorderColor

@Composable
fun TrainingScreen(
    paddingValues: PaddingValues,
    deckId: String,
    source: Source,
    onTrainingResultsClick: (String) -> Unit,
    onBackClick: () -> Unit,
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
                onSkip = { viewModel.recordAnswer(false, "") },
                onExit = { viewModel.exitTraining() },
                onNextCard = { viewModel.moveToNextCardOrFinish() },
                viewModel = viewModel
            )
        }

        is TrainingScreenState.Finished -> {
            if (currentState.totalCardsCompleted != 0) {
                onTrainingResultsClick(currentState.trainingSessionId)
            } else {
                onBackClick()
            }

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
    onNextCard: () -> Unit,
    viewModel: TrainingViewModel
) {
    val currentCard = currentState.cards[currentState.currentCardIndex]

    BackHandler(onBack = onExit)

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = "Тренировка",
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onExit
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentCard.id,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            },
            label = "CardTransition"
        ) { targetKey  ->
            val card = currentState.cards.first { it.id == targetKey }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                QuestionArea(
                    question = card.question,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                when (card.trainingMode) {
                    TrainingMode.MULTIPLE_CHOICE -> MultipleChoiceContent(
                        currentState,
                        onAnswer,
                        onSkip,
                        onNextCard
                    )

                    TrainingMode.TRUE_FALSE -> TrueFalseContent(
                        card,
                        onAnswer,
                        onSkip,
                        onNextCard
                    )

                    TrainingMode.FILL_IN_THE_BLANK -> FillInTheBlankContent(
                        card,
                        onAnswer,
                        viewModel
                    )

                    else -> Text("Неизвестный режим")
                }
            }
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
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Composable
fun MultipleChoiceContent(
    state: TrainingScreenState.Success,
    onAnswer: (Boolean, String?) -> Unit,
    onSkip: () -> Unit,
    onNextCard: () -> Unit,
) {
    val currentCard = state.cards[state.currentCardIndex]
    val shuffledAnswers = rememberSaveable (currentCard.id) {
        (listOf(currentCard.answer) + currentCard.wrongAnswers).shuffled()
    }
    var selectedAnswer by remember {
        mutableStateOf(state.selectedAnswer)
    }
    var isAnswered by remember { mutableStateOf(state.selectedAnswer != null) }
    var showNextButton by remember { mutableStateOf(state.selectedAnswer != null) }

    LaunchedEffect(currentCard.id) {
        selectedAnswer = null
        isAnswered = false
        showNextButton = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
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

        NextOrSkipButton(
            showNextButton = showNextButton,
            onNextCard = {
                isAnswered = false
                selectedAnswer = null
                showNextButton = false
                onNextCard()
            },
            onSkip = {
                onSkip()
                isAnswered = true
                showNextButton = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
        )
    }
}

@Composable
fun TrueFalseContent(
    card: Card,
    onAnswer: (Boolean, String?) -> Unit,
    onSkip: () -> Unit,
    onNextCard: () -> Unit
) {
    var isAnswered by rememberSaveable { mutableStateOf(false) }
    var selectedAnswer by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var showNextButton by rememberSaveable { mutableStateOf(false) }

    val correctAnswer = card.displayedAnswer == card.answer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Возможный ответ:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.displayedAnswer ?: "Ошибка отображения",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val falseContainerColor =
                    getContainerColor(isAnswered, selectedAnswer, false, correctAnswer)
                val trueContainerColor =
                    getContainerColor(isAnswered, selectedAnswer, true, correctAnswer)

                val falseBorderColor =
                    getBorderColor(isAnswered, selectedAnswer, false, correctAnswer)
                val trueBorderColor =
                    getBorderColor(isAnswered, selectedAnswer, true, correctAnswer)

                TrueFalseButton(
                    text = "ЛОЖЬ",
                    containerColor = falseContainerColor,
                    borderColor = falseBorderColor,
                    onClick = {
                        if (!isAnswered) {
                            isAnswered = true
                            showNextButton = true
                            selectedAnswer = false
                            onAnswer(!correctAnswer, card.displayedAnswer)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                TrueFalseButton(
                    text = "ИСТИНА",
                    containerColor = trueContainerColor,
                    borderColor = trueBorderColor,
                    onClick = {
                        if (!isAnswered) {
                            isAnswered = true
                            showNextButton = true
                            selectedAnswer = true
                            onAnswer(correctAnswer, card.displayedAnswer)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            NextOrSkipButton(
                showNextButton = showNextButton,
                onNextCard = {
                    isAnswered = false
                    selectedAnswer = null
                    showNextButton = false
                    onNextCard()
                },
                onSkip = {
                    isAnswered = true
                    showNextButton = true
                    onSkip()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
private fun TrueFalseButton(
    text: String,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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

@Composable
private fun FillInTheBlankContent(
    card: Card,
    onAnswer: (Boolean, String?) -> Unit,
    viewModel: TrainingViewModel
) {
    var userInput by remember(card.id) { mutableStateOf("") }
    var isAnswered by remember(card.id) { mutableStateOf(false) }
    var isCorrect by remember(card.id) { mutableStateOf(false) }

    if (card.partialAnswer != null) {
        Text(text = "${card.partialAnswer}")
    }


    OutlinedTextField(
        value = userInput,
        onValueChange = { userInput = it },
        label = { Text("Введите ответ") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )

    Button(
        onClick = {
            if (!isAnswered) {
                viewModel.checkFillInTheBlankAnswer(
                    userInput = userInput,
                    correctWords = card.missingWords
                ) { result ->
                    isCorrect = result
                    isAnswered = true
                    onAnswer(result, userInput)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Проверить")
    }

    if (isAnswered) {
        Text(
            text = if (isCorrect) "Верно!" else "Неверно!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCorrect) Color.Green else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}