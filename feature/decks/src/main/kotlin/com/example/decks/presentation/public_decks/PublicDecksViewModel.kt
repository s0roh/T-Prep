package com.example.decks.presentation.public_decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decks.domain.entity.Deck
import com.example.decks.domain.usecase.GetPublicDecksFlowUseCase
import com.example.decks.domain.usecase.LoadNextPublicDecksUseCase
import com.example.decks.util.mergeWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PublicDecksViewModel @Inject constructor(
    getPublicDecksFlowUseCase: GetPublicDecksFlowUseCase,
    private val loadNextPublicDecksUseCase: LoadNextPublicDecksUseCase
) : ViewModel() {

    private val publicDecksFlow = getPublicDecksFlowUseCase()
    private val loadNExtDataFlow = MutableSharedFlow<PublicDecksScreenState>()

    private var lastDecks: List<Deck> = emptyList()
    private var previousSize: Int = 0

    val screenState = publicDecksFlow
        .map {currentDecks ->
            lastDecks = currentDecks
            val hasMoreData = currentDecks.size > previousSize
            previousSize = currentDecks.size
            PublicDecksScreenState.Decks(
                decks = currentDecks,
                hasMoreData = hasMoreData
            ) as PublicDecksScreenState
        }
        .onStart { emit(PublicDecksScreenState.Loading) }
        .mergeWith(loadNExtDataFlow)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PublicDecksScreenState.Initial
        )

    fun loadNextPublicDecks() {
        viewModelScope.launch {
            loadNExtDataFlow.emit(
                PublicDecksScreenState.Decks(
                    decks = lastDecks,
                    nextDataIsLoading = true
                )
            )
            loadNextPublicDecksUseCase()
        }
    }
}