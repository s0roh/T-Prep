package com.example.feature.training.presentation.training

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.common.ui.NavigationIconType
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.feature.training.presentation.components.MultipleChoiceAnswerList
import com.example.feature.training.presentation.components.QuestionArea
import com.example.feature.training.presentation.components.TrainingNavigationButton
import com.example.feature.training.presentation.components.TrueFalseAnswerSection
import com.example.feature.training.presentation.components.TrueFalseButtons
import com.example.feature.training.presentation.util.launchShakeAnimation
import com.example.feature.training.presentation.util.playSound
import com.example.feature.training.presentation.util.vibrate
import com.example.training.domain.entity.TrainingCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun TrainingScreen(
    deckId: String,
    source: Source,
    onTrainingResultsClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: TrainingViewModel = hiltViewModel()
    val screenState = viewModel.screenState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = deckId, key2 = source) {
        if (screenState.value is TrainingScreenState.Initial) {
            viewModel.loadTraining(deckId, source)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TrainingUiEvent.PlaySound -> playSound(context, event.isCorrect)
                TrainingUiEvent.VibrateIncorrectAnswer -> vibrate(context)
                TrainingUiEvent.PlayFinishSound -> playSound(
                    context,
                    isCorrect = true,
                    isFinish = true
                )
            }
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
                    onExit = viewModel::exitTraining,
                    onNextCard = viewModel::moveToNextCardOrFinish,
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


@SuppressLint("MissingPermission")
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
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }
    val correctAnswer = currentCard.displayedAnswer == currentCard.answer

    // Общее состояние для всех типов карточек
    var isAnswered by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableStateOf<Any?>(null) }
    var userInput by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var isCorrect by remember { mutableStateOf(false) }

    // Сбрасываем состояние при смене карточки
    LaunchedEffect(currentCard.id) {
        isAnswered = false
        selectedAnswer = null
        userInput = ""
        isButtonEnabled = true
        isCorrect = false
        shakeOffset.snapTo(0f)
        scrollState.scrollTo(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            AnimatedContent(
                targetState = currentCard,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                },
                label = "CardTransition"
            ) { card ->
                Column {
                    currentState.currentCardPictureUri?.let { uri ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .memoryCachePolicy(CachePolicy.DISABLED)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = "Картинка карточки",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    QuestionArea(
                        question = card.question,
                        modifier = Modifier.padding(horizontal = 25.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    when (card.trainingMode) {
                        TrainingMode.MULTIPLE_CHOICE -> MultipleChoiceContent(
                            card = card,
                            isAnswered = isAnswered,
                            selectedAnswer = selectedAnswer as? String,
                            coroutineScope = coroutineScope,
                            shakeOffset = shakeOffset,
                            onAnswerSelected = { answer ->
                                selectedAnswer = answer
                                isAnswered = true

                                val correct = answer == card.answer
                                viewModel.playFeedback(correct)

                                onAnswer(
                                    answer == card.answer,
                                    card.question,
                                    card.answer,
                                    null,
                                    answer,
                                    TrainingMode.MULTIPLE_CHOICE
                                )
                            }
                        )

                        TrainingMode.TRUE_FALSE -> TrueFalseContent(card = card)

                        TrainingMode.FILL_IN_THE_BLANK -> FillInTheBlankContent(
                            card = card,
                            userInput = userInput,
                            onUserInputChanged = { userInput = it },
                            isAnswered = isAnswered,
                            isCorrect = isCorrect
                        )

                        else -> Text("Неизвестный режим")
                    }
                }
            }
        }

        if (currentCard.trainingMode == TrainingMode.TRUE_FALSE) {
            TrueFalseButtons(
                modifier = Modifier.padding(bottom = 30.dp),
                isAnswered = isAnswered,
                selectedAnswer = selectedAnswer as? Boolean,
                correctAnswer = correctAnswer,
                shakeOffset = shakeOffset,
                onAnswerSelected = { answer ->
                    if (!isAnswered) {
                        selectedAnswer = answer
                        isAnswered = true

                        val correct = answer == correctAnswer
                        viewModel.playFeedback(correct)

                        if (!correct) {
                            coroutineScope.launch { launchShakeAnimation(shakeOffset) }
                        }
                        onAnswer(
                            answer == correctAnswer,
                            currentCard.question,
                            currentCard.answer,
                            null,
                            currentCard.displayedAnswer,
                            TrainingMode.TRUE_FALSE
                        )
                    }
                }
            )
        }

        TrainingNavigationButton(
            isAnswered = isAnswered,
            userInput = userInput,
            isButtonEnabled = isButtonEnabled,
            onNextCard = {
                isButtonEnabled = false
                isAnswered = false
                selectedAnswer = null
                userInput = ""
                onNextCard()
            },
            onSkip = {
                isAnswered = true
                onSkip(
                    currentCard.question,
                    currentCard.answer,
                    currentCard.missingWords.joinToString(" "),
                    currentCard.trainingMode!!
                )
            },
            onSubmit = {
                if (currentCard.trainingMode == TrainingMode.FILL_IN_THE_BLANK && userInput.isNotBlank()) {
                    viewModel.checkFillInTheBlankAnswer(
                        userInput = userInput,
                        correctWords = currentCard.missingWords
                    ) { result ->
                        isCorrect = result
                        isAnswered = true
                        viewModel.playFeedback(result)
                        onAnswer(
                            isCorrect,
                            currentCard.question,
                            currentCard.answer,
                            currentCard.missingWords.joinToString(" "),
                            userInput,
                            TrainingMode.FILL_IN_THE_BLANK
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        )
    }
}

@Composable
private fun MultipleChoiceContent(
    modifier: Modifier = Modifier,
    card: TrainingCard,
    isAnswered: Boolean,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    coroutineScope: CoroutineScope,
    shakeOffset: Animatable<Float, AnimationVector1D>,
) {
    val shuffledAnswers = rememberSaveable(card.id) {
        (listOf(card.answer) + card.wrongAnswers).shuffled()
    }

    Column(modifier = modifier) {
        MultipleChoiceAnswerList(
            answers = shuffledAnswers,
            isAnswered = isAnswered,
            selectedAnswer = selectedAnswer,
            correctAnswer = card.answer,
            shakeOffset = shakeOffset,
            onAnswerSelected = { answer ->
                if (!isAnswered) {
                    onAnswerSelected(answer)
                    if (answer != card.answer) {
                        coroutineScope.launch { launchShakeAnimation(shakeOffset) }
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun TrueFalseContent(
    modifier: Modifier = Modifier,
    card: TrainingCard,
) {
    Column(
        modifier = modifier.padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TrueFalseAnswerSection(
            displayedAnswer = card.displayedAnswer,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FillInTheBlankContent(
    card: TrainingCard,
    userInput: String,
    onUserInputChanged: (String) -> Unit,
    isAnswered: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .padding(horizontal = 25.dp)
    ) {
        if (isAnswered) {
            AnswerWithHighlight(card.answer, card.missingWords)
            UserInputWithHighlight(userInput, card.missingWords, isCorrect)
            Spacer(modifier = Modifier.height(20.dp))
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
                onValueChange = onUserInputChanged,
                label = { Text("Введите ответ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )
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

    // Подсвечиваем только слова из missingWords, соединенные в единую строку
    val annotatedAnswer = buildAnnotatedString {
        val answerText = answer
        val missingText = missingWords.joinToString(" ")

        // Индекс для отслеживания начала позиции missingWords в полном ответе
        var currentIndex = 0

        // Перебираем ответ, подсвечиваем только missingWords
        while (currentIndex < answerText.length) {
            // Ищем, где начинается строка, соответствующая missingWords
            val startIndex = answerText.indexOf(missingText, currentIndex)
            if (startIndex != -1) {
                // Если нашли, добавляем до этого места обычный текст
                append(answerText.substring(currentIndex, startIndex))

                // Подсвечиваем missingWords
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(missingText)
                }

                // Обновляем currentIndex для продолжения после подсвеченной строки
                currentIndex = startIndex + missingText.length
            } else {
                // Если больше нет match, добавляем оставшийся текст
                append(answerText.substring(currentIndex))
                break
            }
        }
    }

    Text(
        text = annotatedAnswer,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 40.dp)
    )
}

@Composable
private fun UserInputWithHighlight(
    userInput: String,
    missingWords: List<String>,
    isCorrect: Boolean,
) {
    Text(
        text = "Ваш ответ:",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    val annotatedUserInput = buildAnnotatedString {
        val userInputWords = userInput.split(" ")

        if (isCorrect) {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(userInput)
            }
        } else if (userInput.isBlank()) {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("Вы не дали ответ")
            }
        } else {
            userInputWords.forEachIndexed { index, word ->
                if (missingWords.any { it.equals(word, ignoreCase = true) }) {
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