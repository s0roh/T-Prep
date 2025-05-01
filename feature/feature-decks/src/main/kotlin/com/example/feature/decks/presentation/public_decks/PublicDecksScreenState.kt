package com.example.feature.decks.presentation.public_decks

import androidx.paging.PagingData
import com.example.common.ui.entity.DeckUiModel
import kotlinx.coroutines.flow.Flow

data class PublicDecksScreenState(
    val decks: Flow<PagingData<DeckUiModel>>? = null,
    val sortType: SortType = SortType.LIKES,
    val category: DeckCategory = DeckCategory.ALL,
    val query: String = "",
    val shouldShowTooltip: Boolean = false,
)

enum class SortType(val value: String) {
    LIKES("likes"),
    TRAININGS("trainings")
}

enum class DeckCategory(val value: String?) {
    ALL(null),
    LIKED("favourite")
}