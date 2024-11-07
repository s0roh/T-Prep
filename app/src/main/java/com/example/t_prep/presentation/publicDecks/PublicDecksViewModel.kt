package com.example.t_prep.presentation.publicDecks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t_prep.data.repository.PrepRepositoryImpl
import com.example.t_prep.domain.usecase.GetPublicDecksFlowUseCase
import com.example.t_prep.domain.usecase.LoadNextPublicDecksUseCase
import com.example.t_prep.presentation.extensions.mergeWith
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PublicDecksViewModel : ViewModel() {

    private val repository = PrepRepositoryImpl()
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
        viewModelScope.launch{
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