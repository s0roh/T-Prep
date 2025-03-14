package com.example.feature.decks.presentation.deck_details_statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.decks.domain.usecase.GetDeckTrainingModeStatsUseCase
import com.example.feature.decks.domain.usecase.GetDeckTrainingStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DeckDetailStatisticViewModel @Inject constructor(
    private val getDeckTrainingStatsUseCase: GetDeckTrainingStatsUseCase,
    private val getDeckTrainingModeStatsUseCase: GetDeckTrainingModeStatsUseCase,
) : ViewModel() {

    var screenState =
        MutableStateFlow<DeckDetailsStatisticScreenState>(DeckDetailsStatisticScreenState.Loading)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        screenState.value = DeckDetailsStatisticScreenState.Error
    }


    fun loadStatistic(deckId: String) {
        viewModelScope.launch(exceptionHandler) {
            val deckTrainingStats = getDeckTrainingStatsUseCase(deckId)

            val deckTrainingModeStats = getDeckTrainingModeStatsUseCase(deckId)

            screenState.value = DeckDetailsStatisticScreenState.Success(
                deckTrainingStats = deckTrainingStats,
                deckTrainingModeStats = deckTrainingModeStats
            )
        }
    }
}