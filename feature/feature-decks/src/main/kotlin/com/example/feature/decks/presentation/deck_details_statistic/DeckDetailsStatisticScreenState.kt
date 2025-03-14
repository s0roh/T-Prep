package com.example.feature.decks.presentation.deck_details_statistic

import com.example.history.domain.entity.TrainingModeStats

interface DeckDetailsStatisticScreenState {

    object Loading : DeckDetailsStatisticScreenState

    object Error : DeckDetailsStatisticScreenState

    data class Success(
        val deckTrainingStats: List<Double>,
        val deckTrainingModeStats: List<TrainingModeStats>
    ) : DeckDetailsStatisticScreenState
}
