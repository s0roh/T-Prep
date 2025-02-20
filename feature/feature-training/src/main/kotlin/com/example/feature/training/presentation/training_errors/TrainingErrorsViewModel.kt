package com.example.feature.training.presentation.training_errors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.training.domain.GetDeckNameAndTrainingSessionTimeUseCase
import com.example.feature.training.domain.GetErrorsListUseCase
import com.example.training.domain.entity.TrainingError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TrainingErrorsViewModel @Inject constructor(
    private val getErrorsListUseCase: GetErrorsListUseCase,
    private val getDeckNameAndTrainingSessionTimeUseCase: GetDeckNameAndTrainingSessionTimeUseCase,
) : ViewModel() {

    var errorsList = MutableStateFlow<List<TrainingError>>(emptyList())
        private set

    var trainingSessionTime = MutableStateFlow<Long>(0L)
        private set

    fun loadErrorsData(trainingSessionId: String) {
        viewModelScope.launch{
            errorsList.value = getErrorsListUseCase(trainingSessionId)
            val (_, sessionTime) = getDeckNameAndTrainingSessionTimeUseCase(trainingSessionId)
            trainingSessionTime.value = sessionTime
        }
    }
}