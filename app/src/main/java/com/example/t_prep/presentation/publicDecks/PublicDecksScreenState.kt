package com.example.t_prep.presentation.publicDecks

import com.example.t_prep.domain.entity.Deck

sealed interface PublicDecksScreenState {

    data object Initial: PublicDecksScreenState

    data object Loading: PublicDecksScreenState

    data class Decks(
        val decks: List<Deck>,
        val nextDataIsLoading: Boolean = false
    ): PublicDecksScreenState
}