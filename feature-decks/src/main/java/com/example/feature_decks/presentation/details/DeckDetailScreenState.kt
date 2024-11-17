package com.example.feature_decks.presentation.details

import com.example.data_decks.domain.entity.Deck

internal sealed interface DeckDetailScreenState {
    object Loading : DeckDetailScreenState
    object Error : DeckDetailScreenState
    data class Success(val deck: Deck) : DeckDetailScreenState
}