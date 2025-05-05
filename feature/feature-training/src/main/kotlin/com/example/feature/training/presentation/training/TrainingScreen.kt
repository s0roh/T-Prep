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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.common.ui.NavigationIconType
import com.example.common.util.playSound
import com.example.common.util.vibrate
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.feature.training.R
import com.example.feature.training.presentation.components.AnswerWithHighlight
import com.example.feature.training.presentation.components.MultipleChoiceAnswerList
import com.example.feature.training.presentation.components.QuestionArea
import com.example.feature.training.presentation.components.TrainingNavigationButton
import com.example.feature.training.presentation.components.TrueFalseAnswerSection
import com.example.feature.training.presentation.components.TrueFalseButtons
import com.example.feature.training.presentation.components.UserInputWithHighlight
import com.example.feature.training.presentation.util.launchShakeAnimation
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
                is TrainingUiEvent.PlaySound -> playSound(
                    context = context,
                    soundResId = if (event.isCorrect) R.raw.correct else R.raw.incorrect
                )

                TrainingUiEvent.VibrateIncorrectAnswer -> vibrate(context)
                TrainingUiEvent.PlayFinishSound -> playSound(
                    context = context,
                    soundResId = R.raw.finish_train
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = stringResource(R.string.train),
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
                    val currentCardUri =
                        currentState.preloadedCardPictures[currentState.currentCardIndex]

                    if (!currentCard.attachment.isNullOrBlank() && currentCardUri == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    currentCardUri?.let { uri ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .memoryCachePolicy(CachePolicy.DISABLED)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = stringResource(R.string.image_of_card),
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

                        else -> Text(stringResource(R.string.unknown_mode))
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
            AnswerWithHighlight(card.answer, card.missingWords, card.missingWordStartIndex)
            UserInputWithHighlight(userInput, card.missingWords, isCorrect)
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            if (card.partialAnswer.isNullOrEmpty()) {
                Text(
                    text = stringResource(R.string.give_answer),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.complete_answer),
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
                label = { Text(stringResource(R.string.enter_answer)) },
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