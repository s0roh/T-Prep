package com.example.feature.training.presentation.training

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Deck
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.feature.training.domain.CheckFillInTheBlankAnswerUseCase
import com.example.feature.training.domain.GetCardPictureUseCase
import com.example.feature.training.domain.GetDeckByIdLocalUseCase
import com.example.feature.training.domain.GetDeckByIdNetworkUseCase
import com.example.feature.training.domain.GetTrainingModesUseCase
import com.example.feature.training.domain.PrepareTrainingCardsUseCase
import com.example.feature.training.domain.RecordAnswerUseCase
import com.example.feature.training.domain.RecordTrainingUseCase
import com.example.training.domain.entity.TrainingCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TrainingViewModel @Inject constructor(
    private val getDeckByIdNetworkUseCase: GetDeckByIdNetworkUseCase,
    private val getDeckByIdLocalUseCase: GetDeckByIdLocalUseCase,
    private val prepareTrainingCardsUseCase: PrepareTrainingCardsUseCase,
    private val recordAnswerUseCase: RecordAnswerUseCase,
    private val recordTrainingUseCase: RecordTrainingUseCase,
    private val checkFillInTheBlankAnswerUseCase: CheckFillInTheBlankAnswerUseCase,
    private val getTrainingModesUseCase: GetTrainingModesUseCase,
    private val getCardPictureUseCase: GetCardPictureUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<TrainingScreenState>(TrainingScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        screenState.value = TrainingScreenState.Error(message.toString())
    }

    private var currentDeckId: String = ""
    private lateinit var currentSource: Source
    private lateinit var currentDeck: Deck
    private var correctAnswersCount = 0
    private var cardsCompleted = 0
    private lateinit var trainingSessionId: String

    fun loadTraining(deckId: String, source: Source) {
        currentDeckId = deckId
        currentSource = source
        trainingSessionId = generateTrainingSessionId(currentDeckId)

        viewModelScope.launch(exceptionHandler) {
            screenState.value = TrainingScreenState.Loading

            val cards = loadCardsForTraining(source)
            val currentCard  = cards.firstOrNull()
            val nextCard = cards.getOrNull(1)

            val currentUri = getCardUriIfExists(currentCard)
            val nextUri = getCardUriIfExists(nextCard)

            screenState.value = TrainingScreenState.Success(
                cards = cards,
                currentCardPictureUri = currentUri,
                nextCardPictureUri = nextUri
            )
        }
    }

    private suspend fun loadCardsForTraining(source: Source): List<TrainingCard> {
        currentDeck = when (source) {
            Source.LOCAL -> {
                getDeckByIdLocalUseCase(currentDeckId)
            }

            Source.NETWORK -> {
                val (deck, source) = getDeckByIdNetworkUseCase(currentDeckId)
                currentSource = source
                currentDeckId = deck.id
                deck
            }
        }

        val trainingModes: Set<TrainingMode> = getTrainingModesUseCase(deckId = currentDeckId)
            .modes
            .toSet()

        return prepareTrainingCardsUseCase(
            deckId = currentDeck.id,
            cards = currentDeck.cards,
            modes = trainingModes
        )
    }

    fun checkFillInTheBlankAnswer(
        userInput: String,
        correctWords: List<String>,
        onResult: (Boolean) -> Unit,
    ) {
        viewModelScope.launch(exceptionHandler) {
            val result = checkFillInTheBlankAnswerUseCase(
                userInput = userInput,
                correctWords = correctWords
            )
            onResult(result)
        }
    }

    fun recordAnswer(
        isCorrect: Boolean,
        question: String,
        correctAnswer: String,
        fillInTheBlankAnswer: String? = null,
        selectedAnswer: String? = null,
        trainingMode: TrainingMode,
    ) {
        val currentState = screenState.value as? TrainingScreenState.Success ?: return
        screenState.value = currentState.copy(selectedAnswer = selectedAnswer)
        val currentCard = currentState.cards[currentState.currentCardIndex]

        updateAnswerStats(isCorrect)

        viewModelScope.launch {
            recordAnswerUseCase(
                cardId = currentCard.id,
                isCorrect = isCorrect,
                question = question,
                correctAnswer = correctAnswer,
                fillInTheBlankAnswer = fillInTheBlankAnswer,
                incorrectAnswer = selectedAnswer,
                trainingSessionId = trainingSessionId,
                trainingMode = trainingMode,
                attachment = currentCard.attachment
            )
            recordTrainingUseCase(
                deckId = currentDeck.id,
                deckName = currentDeck.name,
                cardsCount = currentDeck.cards.size,
                source = currentSource,
                trainingSessionId = trainingSessionId
            )
        }
    }

    private fun updateAnswerStats(isCorrect: Boolean) {
        if (isCorrect) correctAnswersCount++
        cardsCompleted++
    }

    fun exitTraining() {
        finishTraining()
    }

    fun moveToNextCardOrFinish() {
        val currentState = screenState.value as? TrainingScreenState.Success ?: return
        val nextIndex = currentState.currentCardIndex + 1
        val cards = currentState.cards

        if (nextIndex < cards.size) {
            val newCurrentUri = currentState.nextCardPictureUri
            val nextNextCard = cards.getOrNull(nextIndex + 1)

            viewModelScope.launch(exceptionHandler) {
                val newNextUri = getCardUriIfExists(nextNextCard)

                screenState.value = currentState.copy(
                    currentCardIndex = nextIndex,
                    correctAnswers = correctAnswersCount,
                    currentCardPictureUri = newCurrentUri,
                    nextCardPictureUri = newNextUri,
                )
            }
        } else {
            finishTraining()
        }
    }

    private fun finishTraining() {
        screenState.value = TrainingScreenState.Finished(
            totalCardsCompleted = cardsCompleted,
            correctAnswers = correctAnswersCount,
            trainingSessionId = trainingSessionId
        )
    }

    private suspend fun getCardUriIfExists(card: TrainingCard?): Uri? {
        return card?.let {
            getCardPictureUseCase(
                deckId = currentDeckId,
                cardId = it.id,
                source = currentSource,
                attachment = it.attachment
            )
        }
    }

    private fun generateTrainingSessionId(deckId: String): String {
        return "$deckId-${System.currentTimeMillis()}"
    }
}