package com.example.feature.decks.presentation.deck_details

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Card
import com.example.common.ui.snackbar_controller.SnackbarAction
import com.example.common.ui.snackbar_controller.SnackbarController
import com.example.common.ui.snackbar_controller.SnackbarEvent
import com.example.database.models.Source
import com.example.feature.decks.domain.usecase.DeleteCardUseCase
import com.example.feature.decks.domain.usecase.DeleteDeckUseCase
import com.example.feature.decks.domain.usecase.GetCardPictureUseCase
import com.example.feature.decks.domain.usecase.GetDeckByIdFromLocalUseCase
import com.example.feature.decks.domain.usecase.GetDeckByIdFromNetworkUseCase
import com.example.feature.decks.domain.usecase.GetNextTrainingTimeUseCase
import com.example.feature.decks.domain.usecase.IsTooltipEnabledUseCase
import com.example.feature.decks.domain.usecase.RestoreDeckUseCase
import com.example.feature.decks.domain.usecase.SetTooltipShownUseCase
import com.example.feature.decks.domain.usecase.SoftDeleteDeckUseCase
import com.example.feature.decks.domain.usecase.UpdateDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("CAST_NEVER_SUCCEEDS")
@HiltViewModel
internal class DeckDetailViewModel @Inject constructor(
    private val getDeckByIdFromNetworkUseCase: GetDeckByIdFromNetworkUseCase,
    private val getDeckByIdFromLocalUseCase: GetDeckByIdFromLocalUseCase,
    private val softDeleteDeckUseCase: SoftDeleteDeckUseCase,
    private val restoreDeckUseCase: RestoreDeckUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getNextTrainingTimeUseCase: GetNextTrainingTimeUseCase,
    private val updateDeckUseCase: UpdateDeckUseCase,
    private val getCardPictureUseCase: GetCardPictureUseCase,
    private val isTooltipEnabledUseCase: IsTooltipEnabledUseCase,
    private val setTooltipShownUseCase: SetTooltipShownUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<DeckDetailScreenState>(DeckDetailScreenState.Loading)
        private set

    private var currentDeckId: String? = null
    private var nextTrainingTime: Long? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        screenState.value = DeckDetailScreenState.Error
    }

    fun loadDeckById(deckId: String, source: Source) {
        when (source) {
            Source.LOCAL -> {
                currentDeckId = deckId
                viewModelScope.launch(exceptionHandler) {
                    nextTrainingTime = getNextTrainingTimeUseCase(deckId = deckId)
                    val shouldShowTooltip = isTooltipEnabledUseCase(source = Source.LOCAL)
                    if (shouldShowTooltip) {
                        setTooltipShownUseCase(source = Source.LOCAL)
                    }
                    getDeckByIdFromLocalUseCase(deckId)?.also { deck ->
                        screenState.value = DeckDetailScreenState.Success(
                            deck = deck,
                            source = Source.LOCAL,
                            nextTrainingTime = nextTrainingTime,
                            shouldShowTooltip = shouldShowTooltip
                        )
                    } ?: throw IllegalStateException("Колода с ID $deckId не найден")
                }
            }

            Source.NETWORK -> {
                viewModelScope.launch(exceptionHandler) {
                    getDeckByIdFromNetworkUseCase(deckId).also { (deck, source) ->
                        currentDeckId = deck.id
                        nextTrainingTime = getNextTrainingTimeUseCase(deckId = deck.id)
                        val shouldShowTooltip = isTooltipEnabledUseCase(source = source)
                        if (shouldShowTooltip) {
                            setTooltipShownUseCase(source = source)
                        }
                        screenState.value = DeckDetailScreenState.Success(
                            deck = deck,
                            source = source,
                            nextTrainingTime = nextTrainingTime,
                            shouldShowTooltip = shouldShowTooltip
                        )
                    }
                }
            }
        }
    }

    fun changeDeckPrivacy() {
        val currentState = screenState.value
        if (currentState !is DeckDetailScreenState.Success) return

        val updatedDeck = currentState.deck.copy(isPublic = !currentState.deck.isPublic)

        viewModelScope.launch(exceptionHandler) {
            updateDeckUseCase(deck = updatedDeck)
            screenState.value = currentState.copy(deck = updatedDeck)
        }
    }

    fun deleteDeckWithUndo() {
        val currentState = screenState.value
        if (currentState !is DeckDetailScreenState.Success) return

        currentDeckId?.let { deckId ->
            viewModelScope.launch(exceptionHandler) {
                softDeleteDeckUseCase(deckId = deckId)
                showSnackbar(deckName = currentState.deck.name)
            }
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch(exceptionHandler) {
            deleteCardUseCase(card)
            currentDeckId?.also {
                getDeckByIdFromLocalUseCase(it)?.also { deck ->
                    screenState.value = DeckDetailScreenState.Success(
                        deck = deck,
                        source = Source.LOCAL,
                        nextTrainingTime = nextTrainingTime
                    )
                }
            }
        }
    }

    private fun showSnackbar(deckName: String) {
        currentDeckId?.let { deckId ->
            viewModelScope.launch {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Колода \"$deckName\" удалена.",
                        action = SnackbarAction(
                            name = "Восстановить",
                            action = {
                                restoreDeckUseCase(deckId = deckId)
                            },
                            dismiss = {
                                deleteDeckUseCase(deckId = deckId)
                            }
                        )
                    )
                )
            }
        }
    }

    fun getCardPicture(
        deckId: String,
        cardId: Int,
        source: Source,
        attachment: String?,
        onResult: (Uri?) -> Unit,
    ) {
        viewModelScope.launch(exceptionHandler) {
            val uri = getCardPictureUseCase(
                deckId = deckId,
                cardId = cardId,
                source = source,
                attachment = attachment
            )

            onResult(uri)
        }
    }
}