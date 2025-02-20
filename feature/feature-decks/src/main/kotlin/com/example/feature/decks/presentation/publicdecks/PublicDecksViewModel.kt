package com.example.feature.decks.presentation.publicdecks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.decks.domain.entity.PublicDeck
import com.example.feature.decks.domain.usecase.GetPublicDecksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class PublicDecksViewModel @Inject constructor(
    getPublicDecksUseCase: GetPublicDecksUseCase,
) : ViewModel() {

    val publicDecks: Flow<PagingData<PublicDeck>> = getPublicDecksUseCase()
        .cachedIn(viewModelScope)
}