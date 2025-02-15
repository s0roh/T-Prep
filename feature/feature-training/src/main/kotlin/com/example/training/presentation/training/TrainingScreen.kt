package com.example.training.presentation.training

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.training.presentation.components.MultipleChoiceButton
import com.example.training.presentation.components.NextOrSkipButton
import com.example.training.presentation.components.QuestionArea
import com.example.training.presentation.components.TrueFalseButton
import com.example.training.presentation.components.getAnswerColor
import com.example.training.presentation.components.getBorderColor
import com.example.training.presentation.components.getContainerColor
import com.example.training.presentation.util.launchShakeAnimation
import kotlinx.coroutines.launch

@Composable
fun TrainingScreen(
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

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = "Тренировка",
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = { viewModel.exitTraining() }
            )
        }
    ) { paddingValues ->

        when (val currentState = screenState.value) {

            is TrainingScreenState.Success -> {
                TrainingCardsContent(
                    paddingValues = paddingValues,
                    currentState = currentState,
                    onAnswer = { isCorrect, answer, trainingMode ->
                        viewModel.recordAnswer(
                            isCorrect,
                            answer,
                            trainingMode
                        )
                    },
                    onSkip = { trainingMode -> viewModel.recordAnswer(false, "", trainingMode) },
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
}


@Composable
private fun TrainingCardsContent(
    paddingValues: PaddingValues,
    currentState: TrainingScreenState.Success,
    onAnswer: (Boolean, String?, TrainingMode) -> Unit,
    onSkip: (TrainingMode) -> Unit,
    onNextCard: () -> Unit,
    viewModel: TrainingViewModel
) {
    val currentCard = currentState.cards[currentState.currentCardIndex]

    AnimatedContent(
        targetState = currentCard,
        transitionSpec = {
            slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
        },
        label = "CardTransition"
    ) { card ->
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
                    card,
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

//                TrainingMode.FILL_IN_THE_BLANK -> FillInTheBlankContent(
//                    card,
//                    onAnswer,
//                    viewModel
//                )

                else -> Text("Неизвестный режим")
            }
        }
    }
}

@Composable
fun MultipleChoiceContent(
    card: Card,
    onAnswer: (Boolean, String?, TrainingMode) -> Unit,
    onSkip: (TrainingMode) -> Unit,
    onNextCard: () -> Unit,
) {
    val shuffledAnswers = rememberSaveable(card.id) {
        (listOf(card.answer) + card.wrongAnswers).shuffled()
    }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(card.id) {
        selectedAnswer = null
        isAnswered = false
        shakeOffset.snapTo(0f)
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
            items(shuffledAnswers) { answer ->

                MultipleChoiceButton(
                    modifier = Modifier
                        .offset(x = shakeOffset.value.dp),
                    answer = answer,
                    containerColor = getAnswerColor(
                        isAnswered,
                        answer,
                        card.answer,
                        selectedAnswer
                    ),
                    borderColor = getBorderColor(isAnswered, answer, card.answer, selectedAnswer),
                    isEnabled = !isAnswered,
                    onClick = {
                        if (!isAnswered) {
                            selectedAnswer = answer
                            isAnswered = true
                            onAnswer(answer == card.answer, answer, TrainingMode.MULTIPLE_CHOICE)
                            if (answer != card.answer) coroutineScope.launch {
                                launchShakeAnimation(
                                    shakeOffset
                                )
                            }
                        }
                    }
                )
            }
        }

        NextOrSkipButton(
            isAnswered = isAnswered,
            onNextCard = {
                isAnswered = false
                selectedAnswer = null
                onNextCard()
            },
            onSkip = {
                onSkip(TrainingMode.MULTIPLE_CHOICE)
                isAnswered = true
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
    onAnswer: (Boolean, String?, TrainingMode) -> Unit,
    onSkip: (TrainingMode) -> Unit,
    onNextCard: () -> Unit
) {
    var isAnswered by rememberSaveable { mutableStateOf(false) }
    var selectedAnswer by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = shakeOffset.value.dp),
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
                            selectedAnswer = false
                            val isCorrect = !correctAnswer
                            onAnswer(isCorrect, card.displayedAnswer, TrainingMode.TRUE_FALSE)

                            if (!isCorrect) {
                                coroutineScope.launch {
                                    launchShakeAnimation(shakeOffset)
                                }
                            }
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
                            selectedAnswer = true
                            val isCorrect = correctAnswer
                            onAnswer(isCorrect, card.displayedAnswer,TrainingMode.TRUE_FALSE)

                            if (!isCorrect) {
                                coroutineScope.launch {
                                    launchShakeAnimation(shakeOffset)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            NextOrSkipButton(
                isAnswered = isAnswered,
                onNextCard = {
                    isAnswered = false
                    selectedAnswer = null
                    onNextCard()
                },
                onSkip = {
                    isAnswered = true
                    onSkip(TrainingMode.TRUE_FALSE)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
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