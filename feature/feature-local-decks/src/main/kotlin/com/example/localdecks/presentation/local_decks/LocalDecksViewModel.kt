package com.example.localdecks.presentation.local_decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.usecase.GetDecksFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class LocalDecksViewModel @Inject constructor(
    getDecksFlowUseCase: GetDecksFlowUseCase
) : ViewModel() {

    val decks: StateFlow<List<Deck>> = getDecksFlowUseCase().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
}