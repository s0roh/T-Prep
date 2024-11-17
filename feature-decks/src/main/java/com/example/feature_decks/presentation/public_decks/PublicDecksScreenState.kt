package com.example.feature_decks.presentation.public_decks

import com.example.data_decks.domain.entity.Deck

internal sealed interface PublicDecksScreenState {

    data object Initial: PublicDecksScreenState

    data object Loading: PublicDecksScreenState

    data class Decks(
        val decks: List<Deck>,
        val nextDataIsLoading: Boolean = false,
        val hasMoreData: Boolean = true
    ): PublicDecksScreenState
}