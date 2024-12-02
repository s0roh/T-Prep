package com.example.localdecks.presentation.add_edit_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Card
import com.example.localdecks.domain.usecase.GetCardByIdUseCase
import com.example.localdecks.domain.usecase.InsertCardUseCase
import com.example.localdecks.domain.usecase.UpdateCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddEditCardViewModel @Inject constructor(
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase
) : ViewModel() {

    var screenState = MutableStateFlow<AddEditCardScreenState>(AddEditCardScreenState())
        private set

    var currentCardId: Long? = null
    var currentDeckId: Long? = null

    fun initCard() {
        viewModelScope.launch {
            currentCardId?.let { id ->
                getCardByIdUseCase(id)?.let { card ->
                    screenState.value = screenState.value.copy(
                        question = card.question,
                        answer = card.answer
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

    fun saveCard(): Boolean {
        screenState.value = screenState.value.copy(isSaveButtonEnabled = false)

        val currentState = screenState.value
        var questionError: String? = null
        var answerError: String? = null

        if (currentState.question.isBlank()) {
            questionError = "Вопрос не может быть пустым"
        }

        if (currentState.answer.isBlank()) {
            answerError = "Ответ не может быть пустым"
        }

        if (questionError != null || answerError != null) {
            screenState.value = screenState.value.copy(
                questionError = questionError,
                answerError = answerError,
                isSaveButtonEnabled = true
            )
            return false
        }

        viewModelScope.launch {
            val card = Card(
                id = currentCardId ?: 0,
                question = currentState.question.trim(),
                answer = currentState.answer.trim()
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
