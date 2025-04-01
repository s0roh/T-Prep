package com.example.feature.decks.presentation.public_decks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.common.ui.entity.DeckUiModel
import com.example.feature.decks.domain.entity.PublicDeckFilters
import com.example.feature.decks.domain.usecase.GetPublicDecksUseCase
import com.example.feature.decks.domain.usecase.LikeOrUnlikeUseCase
import com.example.feature.decks.domain.usecase.SearchPublicDecksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PublicDecksViewModel @Inject constructor(
    getPublicDecksUseCase: GetPublicDecksUseCase,
    private val likeOrUnlikeUseCase: LikeOrUnlikeUseCase,
    private val searchPublicDecksUseCase: SearchPublicDecksUseCase
) : ViewModel() {

    private val filtersFlow = MutableStateFlow(PublicDeckFilters())

    val decksFlow: Flow<PagingData<DeckUiModel>> = getPublicDecksUseCase(filtersFlow)
        .cachedIn(viewModelScope)

    var screenState = MutableStateFlow<PublicDecksScreenState>(PublicDecksScreenState())
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("PublicDecksViewModel", "Error: ${throwable.message}")
    }

    fun onLikeClick(deckId: String, isLiked: Boolean, onUpdate: (Int) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            val updatedLikes = likeOrUnlikeUseCase(deckId, isLiked)
            onUpdate(updatedLikes)
        }
    }

    fun searchPublicDecks(query: String): Flow<PagingData<DeckUiModel>> {
        return searchPublicDecksUseCase(query = query).cachedIn(viewModelScope)
    }

    fun updateSortType(sortType: SortType) {
        screenState.value = screenState.value.copy(sortType = sortType)
        filtersFlow.update { it.copy(sortBy = sortType.value) }
    }

    fun updateCategory(category: DeckCategory) {
        screenState.value = screenState.value.copy(category = category)
        filtersFlow.update { it.copy(category = category.value) }
    }
}