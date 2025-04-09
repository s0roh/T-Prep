package com.example.feature.localdecks.presentation.add_edit_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Card
import com.example.feature.localdecks.domain.usecase.GetCardByIdUseCase
import com.example.feature.localdecks.domain.usecase.InsertCardUseCase
import com.example.feature.localdecks.domain.usecase.UpdateCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddEditCardViewModel @Inject constructor(
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<AddEditCardScreenState>(AddEditCardScreenState())
        private set

    var eventFlow = MutableSharedFlow<AddEditCardEvent>()
        private set

    var currentCardId: Int? = null
    var currentDeckId: String? = null

    fun initCard() {
        viewModelScope.launch {
            currentCardId?.let { id ->
                getCardByIdUseCase(id)?.let { card ->
                    screenState.value = screenState.value.copy(
                        question = card.question,
                        answer = card.answer,
                        wrongAnswerList = card.wrongAnswers
                    )
                }
            }
        }
    }

    fun onQuestionChanged(question: String) {
        screenState.value = screenState.value.copy(question = question, questionError = null)
    }

    fun onAnswerChanged(answer: String) {
        screenState.value = screenState.value.copy(answer = answer, answerError = null)
    }

    fun addWrongAnswerField() {
        val currentList = screenState.value.wrongAnswerList
        if (currentList.size < 3) {
            screenState.value = screenState.value.copy(
                wrongAnswerList = currentList + ""
            )
        }
    }

    fun updateWrongAnswer(index: Int, value: String) {
        val currentList = screenState.value.wrongAnswerList.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = value
            screenState.value = screenState.value.copy(
                wrongAnswerList = currentList
            )
        }
    }

    fun removeWrongAnswer(index: Int) {
        val currentList = screenState.value.wrongAnswerList.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            screenState.value = screenState.value.copy(
                wrongAnswerList = currentList
            )
        }
    }

    fun saveCard(): Boolean {
        screenState.value = screenState.value.copy(isSaveButtonEnabled = false)

        val currentState = screenState.value
        if (currentState.question.isBlank() || currentState.answer.isBlank()) {
            screenState.value = screenState.value.copy(
                questionError = if (currentState.question.isBlank()) "Вопрос не может быть пустым" else null,
                answerError = if (currentState.answer.isBlank()) "Ответ не может быть пустым" else null,
                isSaveButtonEnabled = true
            )
            return false
        }

        val trimmedAnswer = currentState.answer.trim()
        val hasSameWrongAnswer = currentState.wrongAnswerList.any { it.trim() == trimmedAnswer }

        if (hasSameWrongAnswer) {
            viewModelScope.launch {
                eventFlow.emit(AddEditCardEvent.ShowError("Неправильный ответ не должен совпадать с правильным"))
            }
            screenState.value = screenState.value.copy(isSaveButtonEnabled = true)
            return false
        }

        viewModelScope.launch {
            val card = Card(
                id = currentCardId ?: 0,
                question = currentState.question.trim(),
                answer = currentState.answer.trim(),
                wrongAnswers = currentState.wrongAnswerList.filter { it.isNotBlank() }
                    .map { it.trim() }
            )
            if (currentCardId == null) {
                currentDeckId?.also { insertCardUseCase(card = card, deckId = it) }
            } else {
                updateCardUseCase(card)
            }
        }
        return true
    }
}
