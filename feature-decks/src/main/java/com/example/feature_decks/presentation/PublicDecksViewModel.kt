package com.example.feature_decks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_decks.data.repository.DeckRepositoryImpl
import com.example.feature_decks.domain.usecase.GetPublicDecksFlowUseCase
import com.example.feature_decks.domain.usecase.LoadNextPublicDecksUseCase
import com.example.feature_decks.util.mergeWith
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class PublicDecksViewModel : ViewModel() {

    private val repository = DeckRepositoryImpl()
    private val getPublicDecksFlowUseCase = GetPublicDecksFlowUseCase(repository)
    private val loadNextPublicDecksUseCase = LoadNextPublicDecksUseCase(repository)

    private val publicDecksFlow = getPublicDecksFlowUseCase()
    private val loadNExtDataFlow = MutableSharedFlow<PublicDecksScreenState>()

    val screenState = publicDecksFlow
        .filter { it.isNotEmpty() }
        .map { PublicDecksScreenState.Decks(decks = it) as PublicDecksScreenState }
        .onStart { emit(PublicDecksScreenState.Loading) }
        .mergeWith(loadNExtDataFlow)

    fun loadNextPublicDecks() {
        viewModelScope.launch {
            loadNExtDataFlow.emit(
                PublicDecksScreenState.Decks(
                    decks = publicDecksFlow.value,
                    nextDataIsLoading = true
                )
            )
            loadNextPublicDecksUseCase()
        }
    }
}