package com.example.feature.decks.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Card
import com.example.database.models.Source
import com.example.feature.decks.domain.usecase.DeleteCardUseCase
import com.example.feature.decks.domain.usecase.DeleteDeckUseCase
import com.example.feature.decks.domain.usecase.GetDeckByIdFromLocalUseCase
import com.example.feature.decks.domain.usecase.GetDeckByIdFromNetworkUseCase
import com.example.feature.decks.domain.usecase.GetNextTrainingTimeUseCase
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
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getNextTrainingTimeUseCase: GetNextTrainingTimeUseCase,
    private val updateDeckUseCase: UpdateDeckUseCase,
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
                viewModelScope.launch {
                    nextTrainingTime = getNextTrainingTimeUseCase(
                        deckId = deckId,
                        source = source
                    )
                    getDeckByIdFromLocalUseCase(deckId)?.also { deck ->
                        screenState.value = DeckDetailScreenState.Success(
                            deck = deck,
                            source = Source.LOCAL,
                            nextTrainingTime = nextTrainingTime
                        )
                    }
                }
            }

            Source.NETWORK -> {
                viewModelScope.launch(exceptionHandler) {

                    getDeckByIdFromNetworkUseCase(deckId).also { (deck, source) ->
                        currentDeckId = deck.id
                        nextTrainingTime = getNextTrainingTimeUseCase(
                            deckId = deck.id,
                            source = source
                        )
                        screenState.value = DeckDetailScreenState.Success(
                            deck = deck,
                            source = source,
                            nextTrainingTime = nextTrainingTime
                        )
                    }
                }
            }
        }
    }


    fun changeDeckPrivacy() {
        val currentState = screenState.value
        if (currentState !is DeckDetailScreenState.Success) {
            throw IllegalStateException("deleteDeck called in an invalid state")
        }

        val updatedDeck = currentState.deck.copy(isPublic = !currentState.deck.isPublic)

        viewModelScope.launch(exceptionHandler) {
            updateDeckUseCase(deck = updatedDeck)
            screenState.value = currentState.copy(deck = updatedDeck)
        }
    }


    fun deleteDeck() {
        val currentState = screenState.value
        if (currentState !is DeckDetailScreenState.Success) {
            throw IllegalStateException("deleteDeck called in an invalid state")
        }

        viewModelScope.launch(exceptionHandler) {
            deleteDeckUseCase(currentState.deck)
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
}