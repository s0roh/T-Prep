package com.example.feature.training.presentation.training

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.common.ui.NavigationIconType
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.feature.training.presentation.components.MultipleChoiceAnswerList
import com.example.feature.training.presentation.components.NextOrSkipButton
import com.example.feature.training.presentation.components.QuestionArea
import com.example.feature.training.presentation.components.TrueFalseAnswerSection
import com.example.feature.training.presentation.components.TrueFalseButtons
import com.example.feature.training.presentation.util.handleAnswerSelection
import com.example.feature.training.presentation.util.launchShakeAnimation
import com.example.training.domain.entity.TrainingCard
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
                    onAnswer = { isCorrect, question, correctAnswer, fillInTheBlankAnswer, selectedAnswer, trainingMode ->
                        viewModel.recordAnswer(
                            isCorrect = isCorrect,
                            question = question,
                            correctAnswer = correctAnswer,
                            fillInTheBlankAnswer = fillInTheBlankAnswer,
                            selectedAnswer = selectedAnswer,
                            trainingMode = trainingMode
                        )
                    },
                    onSkip = { question, correctAnswer, fillInTheBlankAnswer, trainingMode ->
                        viewModel.recordAnswer(
                            isCorrect = false,
                            question = question,
                            correctAnswer = correctAnswer,
                            fillInTheBlankAnswer = fillInTheBlankAnswer,
                            selectedAnswer = "",
                            trainingMode = trainingMode
                        )
                    },
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
}


@Composable
private fun TrainingCardsContent(
    paddingValues: PaddingValues,
    currentState: TrainingScreenState.Success,
    onAnswer: (Boolean, String, String, String?, String?, TrainingMode) -> Unit,
    onSkip: (String, String, String?, TrainingMode) -> Unit,
    onExit: () -> Unit,
    onNextCard: () -> Unit,
    viewModel: TrainingViewModel,
) {
    BackHandler(onBack = onExit)
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

                TrainingMode.FILL_IN_THE_BLANK -> FillInTheBlankContent(
                    card,
                    onAnswer,
                    onSkip,
                    onNextCard,
                    viewModel
                )

                else -> Text("Неизвестный режим")
            }
        }
    }
}

@Composable
private fun MultipleChoiceContent(
    card: TrainingCard,
    onAnswer: (Boolean, String, String, String?, String?, TrainingMode) -> Unit,
    onSkip: (String, String, String?, TrainingMode) -> Unit,
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
        modifier = Modifier.fillMaxSize()
    ) {
        MultipleChoiceAnswerList(
            answers = shuffledAnswers,
            isAnswered = isAnswered,
            selectedAnswer = selectedAnswer,
            correctAnswer = card.answer,
            shakeOffset = shakeOffset,
            onAnswerSelected = { answer ->
                handleAnswerSelection(
                    card, answer, onAnswer, shakeOffset, coroutineScope,
                    onSelected = { selectedAnswer = it; isAnswered = true }
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )

        NextOrSkipButton(
            isAnswered = isAnswered,
            onNextCard = {
                isAnswered = false
                selectedAnswer = null
                onNextCard()
            },
            onSkip = {
                onSkip(card.question, card.answer, null, TrainingMode.MULTIPLE_CHOICE)
                isAnswered = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp, bottom = 20.dp)
        )
    }
}

@Composable
private fun TrueFalseContent(
    card: TrainingCard,
    onAnswer: (Boolean, String, String, String?, String?, TrainingMode) -> Unit,
    onSkip: (String, String, String?, TrainingMode) -> Unit,
    onNextCard: () -> Unit,
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TrueFalseAnswerSection(
            displayedAnswer = card.displayedAnswer,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        TrueFalseButtons(
            isAnswered = isAnswered,
            selectedAnswer = selectedAnswer,
            correctAnswer = correctAnswer,
            shakeOffset = shakeOffset,
            onAnswerSelected = { answer ->
                if (!isAnswered) {
                    isAnswered = true
                    selectedAnswer = answer
                    val isCorrect = answer == correctAnswer
                    onAnswer(
                        isCorrect,
                        card.question,
                        card.answer,
                        null,
                        card.displayedAnswer,
                        TrainingMode.TRUE_FALSE
                    )
                    if (!isCorrect) {
                        coroutineScope.launch { launchShakeAnimation(shakeOffset) }
                    }
                }
            }
        )

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
                onSkip(card.question, card.answer, null, TrainingMode.TRUE_FALSE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun FillInTheBlankContent(
    card: TrainingCard,
    onAnswer: (Boolean, String, String, String?, String?, TrainingMode) -> Unit,
    onSkip: (String, String, String?, TrainingMode) -> Unit,
    onNextCard: () -> Unit,
    viewModel: TrainingViewModel,
) {
    var userInput by remember(card.id) { mutableStateOf("") }
    var isAnswered by remember(card.id) { mutableStateOf(false) }
    var isCorrect by remember(card.id) { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp)
            .imePadding()
    ) {
        if (isAnswered) {
            AnswerWithHighlight(card.answer, card.missingWords)

            UserInputWithHighlight(userInput, card.missingWords)

            Spacer(modifier = Modifier.height(20.dp))

            // Логика подсветки правильных/неправильных ответов
            if (isCorrect) {
                Text(
                    text = "Ответ правильный!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else if (userInput.isNotBlank()) {
                Text(
                    text = "Ответ неправильный!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        } else {
            if (card.partialAnswer.isNullOrEmpty()) {
                Text(
                    text = "Дайте ответ на вопрос:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "Дополните ответ:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                card.partialAnswer?.let { partialAnswerText ->
                    Text(
                        text = partialAnswerText,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Введите ответ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(focusRequester),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (userInput.isNotBlank()) {
                            viewModel.checkFillInTheBlankAnswer(
                                userInput = userInput,
                                correctWords = card.missingWords
                            ) { result ->
                                isCorrect = result
                                isAnswered = true
                                onAnswer(
                                    isCorrect,
                                    card.question,
                                    card.answer,
                                    card.missingWords.joinToString(" "),
                                    userInput,
                                    TrainingMode.FILL_IN_THE_BLANK
                                )
                            }
                        } else {
                            isAnswered = true
                            onSkip(
                                card.question,
                                card.answer,
                                card.missingWords.joinToString(" "),
                                TrainingMode.FILL_IN_THE_BLANK
                            )
                        }
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val buttonText = when {
            isAnswered -> "Далее"
            userInput.isNotBlank() -> "Проверить ответ"
            else -> "Пропустить"
        }

        Button(
            onClick = {
                when {
                    isAnswered -> {
                        isAnswered = false
                        userInput = ""
                        onNextCard()
                    }

                    userInput.isNotBlank() -> {
                        viewModel.checkFillInTheBlankAnswer(
                            userInput = userInput,
                            correctWords = card.missingWords
                        ) { result ->
                            isCorrect = result
                            isAnswered = true
                            onAnswer(
                                isCorrect,
                                card.question,
                                card.answer,
                                card.missingWords.joinToString(" "),
                                userInput,
                                TrainingMode.FILL_IN_THE_BLANK
                            )
                        }
                    }

                    else -> {
                        isAnswered = true
                        onSkip(
                            card.question,
                            card.answer,
                            card.missingWords.joinToString(" "),
                            TrainingMode.FILL_IN_THE_BLANK
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = buttonText)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun AnswerWithHighlight(answer: String, missingWords: List<String>) {
    Text(
        text = "Ответ:",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // Подсвечиваем слова из missingWords в полном ответе
    val annotatedAnswer = buildAnnotatedString {
        val answerWords = answer.split(" ")
        answerWords.forEachIndexed { index, word ->
            if (missingWords.contains(word)) {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(word)
                }
            } else {
                append(word)
            }
            if (index < answerWords.size - 1) append(" ")
        }
    }

    Text(
        text = annotatedAnswer,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 40.dp)
    )
}

@Composable
private fun UserInputWithHighlight(userInput: String, missingWords: List<String>) {
    Text(
        text = "Ваш ответ:",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    val annotatedUserInput = buildAnnotatedString {
        val userInputWords = userInput.split(" ")
        if (userInput.isBlank()) {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("Вы не дали ответ")
            }
        } else {
            userInputWords.forEachIndexed { index, word ->
                if (missingWords.contains(word)) {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(word)
                    }
                } else {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append(word)
                    }
                }
                if (index < userInputWords.size - 1) append(" ")
            }
        }
    }

    Text(
        text = annotatedUserInput,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}