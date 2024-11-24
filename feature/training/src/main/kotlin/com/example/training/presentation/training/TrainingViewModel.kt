package com.example.training.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Card
import com.example.database.models.Source
import com.example.training.domain.GetDeckByIdUseCase
import com.example.training.domain.PrepareTrainingCardsUseCase
import com.example.training.domain.RecordAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TrainingViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val prepareTrainingCardsUseCase: PrepareTrainingCardsUseCase,
    private val recordAnswerUseCase: RecordAnswerUseCase
) : ViewModel() {

    var screenState = MutableStateFlow<TrainingScreenState>(TrainingScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        screenState.value = TrainingScreenState.Error(message.toString())
    }

    private var currentDeckId: Long = -1L
    private lateinit var currentSource: Source
    private var correctAnswersCount = 0
    private var cardsCompleted = 0

    fun loadTraining(deckId: Long, source: Source) {
        currentDeckId = deckId
        currentSource = source

        viewModelScope.launch(exceptionHandler) {
            screenState.value = TrainingScreenState.Loading

            val cards = loadCardsForTraining(source)

            screenState.value = TrainingScreenState.Success(cards = cards)
        }
    }

    private suspend fun loadCardsForTraining(source: Source): List<Card> {
        return when (source) {
            Source.LOCAL -> {
                // TODO сделать загрузку локальных карточек
                throw NotImplementedError("Загрузка локальных карточек еще не реализована")
            }

            Source.NETWORK -> {
                prepareTrainingCardsUseCase(
                    deckId = currentDeckId,
                    cards = getDeckByIdUseCase(currentDeckId).cards,
                    source = currentSource
                )
            }
        }
    }

    fun recordAnswer(isCorrect: Boolean, selectedAnswer: String? = null) {
        val currentState = screenState.value as? TrainingScreenState.Success ?: return
        screenState.value = currentState.copy(selectedAnswer = selectedAnswer)
        val currentCard = currentState.cards[currentState.currentCardIndex]

        updateAnswerStats(isCorrect)

        viewModelScope.launch {
            recordAnswerUseCase(
                deckId = currentDeckId,
                cardId = currentCard.id,
                isCorrect = isCorrect,
                source = currentSource
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

        if (nextIndex < currentState.cards.size) {
            screenState.value = currentState.copy(
                currentCardIndex = nextIndex,
                correctAnswers = correctAnswersCount
            )
        } else {
            finishTraining()
        }
    }

    private fun finishTraining() {
        screenState.value = TrainingScreenState.Finished(
            totalCardsCompleted = cardsCompleted,
            correctAnswers = correctAnswersCount
        )
    }
}