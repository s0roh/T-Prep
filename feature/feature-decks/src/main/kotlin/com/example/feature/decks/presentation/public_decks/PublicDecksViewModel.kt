package com.example.feature.decks.presentation.public_decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.common.ui.entity.DeckUiModel
import com.example.feature.decks.domain.usecase.GetPublicDecksUseCase
import com.example.feature.decks.domain.usecase.SearchPublicDecksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class PublicDecksViewModel @Inject constructor(
    getPublicDecksUseCase: GetPublicDecksUseCase,
    private val searchPublicDecksUseCase: SearchPublicDecksUseCase,
) : ViewModel() {

    val publicDecks: Flow<PagingData<DeckUiModel>> = getPublicDecksUseCase()
        .cachedIn(viewModelScope)

    fun searchPublicDecks(query: String): Flow<PagingData<DeckUiModel>> {
        return searchPublicDecksUseCase(query = query).cachedIn(viewModelScope)
    }
}