package com.example.feature.training.presentation.training_results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.models.Source
import com.example.feature.training.domain.GetDeckNameAndTrainingSessionTimeUseCase
import com.example.feature.training.domain.GetErrorsListUseCase
import com.example.feature.training.domain.GetInfoForNavigationToDeckUseCase
import com.example.feature.training.domain.GetNextTrainingTimeUseCase
import com.example.feature.training.domain.GetTotalAndCorrectCountAnswersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TrainingResultsViewModel @Inject constructor(
    private val getErrorsListUseCase: GetErrorsListUseCase,
    private val getNextTrainingTimeUseCase: GetNextTrainingTimeUseCase,
    private val getTotalAndCorrectCountAnswersUseCase: GetTotalAndCorrectCountAnswersUseCase,
    private val getDeckNameAndTrainingSessionTimeUseCase: GetDeckNameAndTrainingSessionTimeUseCase,
    private val getInfoForNavigationToDeckUseCase: GetInfoForNavigationToDeckUseCase
) : ViewModel() {

    var screenState =
        MutableStateFlow<TrainingResultsScreenState>(TrainingResultsScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        screenState.value = TrainingResultsScreenState.Error(message.toString())
    }

    fun loadTrainingData(trainingSessionId: String) {
        viewModelScope.launch(exceptionHandler) {
            screenState.value = TrainingResultsScreenState.Loading

            val (totalAnswers, correctAnswers) =
                getTotalAndCorrectCountAnswersUseCase(trainingSessionId)

            val (deckName, trainingSessionTime) =
                getDeckNameAndTrainingSessionTimeUseCase(trainingSessionId)

            val correctPercentage =
                if (totalAnswers == 0) 0 else (correctAnswers * 100) / totalAnswers
            val incorrectPercentage = if (totalAnswers == 0) 0 else 100 - correctPercentage

            val nextTrainingTime = getNextTrainingTimeUseCase(trainingSessionId)

            val errorsList = getErrorsListUseCase(trainingSessionId)

            screenState.value = TrainingResultsScreenState.Success(
                deckName = deckName,
                trainingSessionTime = trainingSessionTime,
                totalAnswers = totalAnswers,
                correctAnswers = correctAnswers,
                incorrectPercentage = incorrectPercentage,
                correctPercentage = correctPercentage,
                nextTrainingTime = nextTrainingTime,
                errorsList = errorsList
            )
        }
    }

    fun getInfoForNavigationToDeck(
        trainingSessionId: String,
        onResult: (Pair<String, Source>) -> Unit
    ) {
        viewModelScope.launch(exceptionHandler) {
            val result = getInfoForNavigationToDeckUseCase(trainingSessionId = trainingSessionId)
            onResult(result)
        }
    }
}