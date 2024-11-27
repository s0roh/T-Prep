package com.example.decks.presentation.publicdecks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.common.domain.entity.Deck
import com.example.decks.domain.usecase.GetPublicDecksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class PublicDecksViewModel @Inject constructor(
    getPublicDecksUseCase: GetPublicDecksUseCase
) : ViewModel() {

    val publicDecks: Flow<PagingData<Deck>> = getPublicDecksUseCase()
        .cachedIn(viewModelScope)
}