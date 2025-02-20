package com.example.decks.presentation.details

import com.example.common.domain.entity.Deck


internal sealed interface DeckDetailScreenState {

    object Loading : DeckDetailScreenState

    object Error : DeckDetailScreenState

    data class Success(
        val deck: Deck,
        val nextTrainingTime: Long?
    ) : DeckDetailScreenState
}