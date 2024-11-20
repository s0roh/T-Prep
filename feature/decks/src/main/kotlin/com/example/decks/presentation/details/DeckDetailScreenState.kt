package com.example.decks.presentation.details

import com.example.decks.domain.entity.Deck

internal sealed interface DeckDetailScreenState {
    object Loading : DeckDetailScreenState
    object Error : DeckDetailScreenState
    data class Success(val deck: Deck) : DeckDetailScreenState
}