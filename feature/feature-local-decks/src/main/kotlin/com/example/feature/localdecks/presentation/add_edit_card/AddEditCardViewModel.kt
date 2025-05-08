package com.example.feature.localdecks.presentation.add_edit_card

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Card
import com.example.feature.localdecks.domain.usecase.DeleteCardPictureUseCase
import com.example.feature.localdecks.domain.usecase.GetCardByIdUseCase
import com.example.feature.localdecks.domain.usecase.GetCardPictureUseCase
import com.example.feature.localdecks.domain.usecase.InsertCardUseCase
import com.example.feature.localdecks.domain.usecase.UpdateCardPictureUseCase
import com.example.feature.localdecks.domain.usecase.UpdateCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddEditCardViewModel @Inject constructor(
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val updateCardPictureUseCase: UpdateCardPictureUseCase,
    private val getCardPictureUseCase: GetCardPictureUseCase,
    private val deleteCardPictureUseCase: DeleteCardPictureUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<AddEditCardScreenState>(AddEditCardScreenState())
        private set

    var eventFlow = MutableSharedFlow<AddEditCardEvent>()
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        eventFlow.tryEmit(AddEditCardEvent.ShowError("Ошибка: ${message.message}"))
        screenState.value = screenState.value.copy(isSaveButtonEnabled = true)
    }

    var currentCardId: Int? = null
    var currentDeckId: String? = null
    private var originalCardPictureUri: Uri? = null

    fun initCard() {
        viewModelScope.launch(exceptionHandler) {
            currentCardId?.let { id ->
                getCardByIdUseCase(id)?.let { card ->
                    val pictureUri = currentDeckId?.let { deckId ->
                        getCardPictureUseCase(deckId = deckId, cardId = id)
                    }

                    originalCardPictureUri = pictureUri

                    screenState.value = screenState.value.copy(
                        question = card.question,
                        answer = card.answer,
                        wrongAnswerList = card.wrongAnswers,
                        attachment = card.attachment,
                        picturePath = card.picturePath,
                        cardPictureUri = pictureUri
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
        val trimmedWrongAnswers = currentState.wrongAnswerList
            .filter { it.isNotBlank() }
            .map { it.trim() }

        // Проверка: неправильный ответ совпадает с правильным
        val hasSameWrongAnswer = trimmedWrongAnswers.any { it == trimmedAnswer }
        if (hasSameWrongAnswer) {
            viewModelScope.launch {
                eventFlow.emit(AddEditCardEvent.ShowError("Неправильный ответ не должен совпадать с правильным"))
            }
            screenState.value = screenState.value.copy(isSaveButtonEnabled = true)
            return false
        }

        // Проверка: есть дубликаты в неправильных ответах
        val hasDuplicateWrongAnswers = trimmedWrongAnswers.size != trimmedWrongAnswers.toSet().size
        if (hasDuplicateWrongAnswers) {
            viewModelScope.launch {
                eventFlow.emit(AddEditCardEvent.ShowError("Неправильные ответы не должны повторяться"))
            }
            screenState.value = screenState.value.copy(isSaveButtonEnabled = true)
            return false
        }

        viewModelScope.launch(exceptionHandler) {
            val localDeckId = currentDeckId

            val card = Card(
                id = currentCardId ?: 0,
                question = normalizeSpaces(currentState.question),
                answer = normalizeSpaces(currentState.answer),
                wrongAnswers = currentState.wrongAnswerList.filter { it.isNotBlank() }
                    .map { it.trim() },
                attachment = currentState.attachment,
                picturePath = currentState.picturePath
            )
            val cardId: Int = if (currentCardId == null) {
                val newCardId = localDeckId?.let {
                    insertCardUseCase(card = card, deckId = it)
                }
                newCardId ?: throw Exception("Не удалось создать карточку")
            } else {
                updateCardUseCase(card)
                card.id
            }

            if (localDeckId != null) {
                if (currentState.cardPictureUri == null && originalCardPictureUri != null) {
                    deleteCardPictureUseCase(deckId = localDeckId, cardId = cardId)
                } else if (currentState.cardPictureUri != null && currentState.cardPictureUri != originalCardPictureUri) {
                    updateCardPictureUseCase(
                        deckId = localDeckId,
                        cardId = cardId,
                        pictureUri = currentState.cardPictureUri
                    )
                }
            }
        }
        return true
    }

    fun setCardPicture(uri: String) {
        viewModelScope.launch(exceptionHandler) {
            screenState.value = screenState.value.copy(cardPictureUri = uri.toUri())
        }
    }

    fun deleteCardPicture() {
        viewModelScope.launch(exceptionHandler) {
            screenState.value = screenState.value.copy(cardPictureUri = null)
        }
    }

    private fun normalizeSpaces(text: String): String {
        return text.replace(Regex("\\s+"), " ").trim()
    }
}
