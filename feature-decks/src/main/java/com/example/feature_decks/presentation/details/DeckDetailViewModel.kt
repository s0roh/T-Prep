package com.example.feature_decks.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_decks.domain.usecase.GetDeckByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DeckDetailViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase
) : ViewModel() {

    var screenState = MutableStateFlow<DeckDetailScreenState>(DeckDetailScreenState.Loading)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        screenState.value = DeckDetailScreenState.Error
    }

    fun loadDeckById(deckId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val deck = getDeckByIdUseCase(deckId)
            screenState.value = DeckDetailScreenState.Success(deck)
        }
    }
}