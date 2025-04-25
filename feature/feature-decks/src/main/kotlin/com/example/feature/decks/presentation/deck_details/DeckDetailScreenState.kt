package com.example.feature.decks.presentation.deck_details

import com.example.common.domain.entity.Deck
import com.example.database.models.Source


internal sealed interface DeckDetailScreenState {

    object Loading : DeckDetailScreenState

    object Error : DeckDetailScreenState

    data class Success(
        val deck: Deck,
        val source: Source,
        val shouldShowTooltip: Boolean = false,
        val nextTrainingTime: Long?,
    ) : DeckDetailScreenState
}