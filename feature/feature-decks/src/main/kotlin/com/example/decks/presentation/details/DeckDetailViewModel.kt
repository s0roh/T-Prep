package com.example.decks.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.database.models.Source
import com.example.decks.domain.usecase.DeleteCardUseCase
import com.example.decks.domain.usecase.DeleteDeckUseCase
import com.example.decks.domain.usecase.GetDeckByIdFromLocalUseCase
import com.example.decks.domain.usecase.GetDeckByIdFromNetworkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DeckDetailViewModel @Inject constructor(
    private val getDeckByIdFromNetworkUseCase: GetDeckByIdFromNetworkUseCase,
    private val getDeckByIdFromLocalUseCase: GetDeckByIdFromLocalUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val deleteCardUseCase: DeleteCardUseCase
) : ViewModel() {

    var screenState = MutableStateFlow<DeckDetailScreenState>(DeckDetailScreenState.Loading)
        private set

    private var currentDeckId: Long? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        screenState.value = DeckDetailScreenState.Error
    }

    fun loadDeckById(deckId: Long, source: Source) {
        when (source) {
            Source.LOCAL -> {
                currentDeckId = deckId
                viewModelScope.launch {
                    getDeckByIdFromLocalUseCase(deckId)?.also {
                        screenState.value = DeckDetailScreenState.Success(it)
                    }
                }
            }

            Source.NETWORK -> {
                viewModelScope.launch(exceptionHandler) {
                    getDeckByIdFromNetworkUseCase(deckId).also {
                        screenState.value = DeckDetailScreenState.Success(it)
                    }
                }
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch(exceptionHandler) {
            deleteDeckUseCase(deck)
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch(exceptionHandler) {
            deleteCardUseCase(card)
            currentDeckId?.also {
                getDeckByIdFromLocalUseCase(it)?.also {
                    screenState.value = DeckDetailScreenState.Success(it)
                }
            }
        }
    }
}