package com.example.feature.decks.presentation.public_decks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import com.example.database.models.Source
import com.example.feature.decks.domain.entity.PublicDeckFilters
import com.example.feature.decks.domain.usecase.GetDeckByIdFromNetworkUseCase
import com.example.feature.decks.domain.usecase.GetPublicDecksUseCase
import com.example.feature.decks.domain.usecase.IncrementFavouriteFilterButtonMetricUseCase
import com.example.feature.decks.domain.usecase.IsPublicDecksTooltipEnabledUseCase
import com.example.feature.decks.domain.usecase.LikeOrUnlikeUseCase
import com.example.feature.decks.domain.usecase.SearchPublicDecksUseCase
import com.example.feature.decks.domain.usecase.SetPublicDecksTooltipShownUseCase
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
    private val searchPublicDecksUseCase: SearchPublicDecksUseCase,
    private val getDeckByIdFromNetworkUseCase: GetDeckByIdFromNetworkUseCase,
    private val incrementFavouriteFilterButtonMetricUseCase: IncrementFavouriteFilterButtonMetricUseCase,
    isPublicDecksTooltipEnabledUseCase: IsPublicDecksTooltipEnabledUseCase,
    setPublicDecksTooltipShownUseCase: SetPublicDecksTooltipShownUseCase,
) : ViewModel() {

    private val filtersFlow = MutableStateFlow(PublicDeckFilters())

    val decksFlow: Flow<PagingData<DeckUiModel>> = getPublicDecksUseCase(filtersFlow)
        .cachedIn(viewModelScope)

    var screenState = MutableStateFlow<PublicDecksScreenState>(PublicDecksScreenState())
        private set

    init {
        val shouldShowTooltip = isPublicDecksTooltipEnabledUseCase()
        if (shouldShowTooltip) setPublicDecksTooltipShownUseCase()
        screenState.value = screenState.value.copy(shouldShowTooltip = shouldShowTooltip)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("PublicDecksViewModel", "Error: ${throwable.message}")
    }

    fun onLikeClick(deckId: String, isLiked: Boolean, onUpdate: (Boolean, Int) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            val updatedLikes = likeOrUnlikeUseCase(deckId, isLiked)
            onUpdate(!isLiked, updatedLikes)
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

        if (category == DeckCategory.LIKED) {
            incrementFavouriteFilterButtonMetricUseCase()
        }
    }

    suspend fun getDeckById(deckId: String): Pair<Deck, Source> {
        return getDeckByIdFromNetworkUseCase(deckId)
    }
}