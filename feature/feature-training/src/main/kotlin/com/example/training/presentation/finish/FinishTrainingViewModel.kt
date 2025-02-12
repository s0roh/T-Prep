package com.example.training.presentation.finish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.training.domain.GetDeckNameAndTrainingSessionTimeUseCase
import com.example.training.domain.GetErrorsListUseCase
import com.example.training.domain.GetNextTrainingTimeUseCase
import com.example.training.domain.GetTotalAndCorrectCountAnswersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FinishTrainingViewModel @Inject constructor(
    private val getErrorsListUseCase: GetErrorsListUseCase,
    private val getNextTrainingTimeUseCase: GetNextTrainingTimeUseCase,
    private val getTotalAndCorrectCountAnswersUseCase: GetTotalAndCorrectCountAnswersUseCase,
    private val getDeckNameAndTrainingSessionTimeUseCase: GetDeckNameAndTrainingSessionTimeUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<FinishTrainingScreenState>(FinishTrainingScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        screenState.value = FinishTrainingScreenState.Error(message.toString())
    }

    fun loadTrainingData(trainingSessionId: String) {
        viewModelScope.launch(exceptionHandler) {
            screenState.value = FinishTrainingScreenState.Loading

            val (totalAnswers, correctAnswers) =
                getTotalAndCorrectCountAnswersUseCase(trainingSessionId)

            val (deckName, trainingSessionTime) =
                getDeckNameAndTrainingSessionTimeUseCase(trainingSessionId)

            val correctPercentage =
                if (totalAnswers == 0) 0 else (correctAnswers * 100) / totalAnswers
            val incorrectPercentage = if (totalAnswers == 0) 0 else 100 - correctPercentage

            val nextTrainingTime = getNextTrainingTimeUseCase(trainingSessionId)

            val errorsList = getErrorsListUseCase(trainingSessionId)

            screenState.value = FinishTrainingScreenState.Success(
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
}