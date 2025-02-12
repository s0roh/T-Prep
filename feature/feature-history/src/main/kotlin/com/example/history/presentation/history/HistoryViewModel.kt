package com.example.history.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.usecase.GetTrainingHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    private val getTrainingHistoryUseCase: GetTrainingHistoryUseCase
) : ViewModel() {

    var historyGroups: MutableStateFlow<List<TrainingHistoryItem>> = MutableStateFlow(emptyList())
        private set

    fun loadHistory() {
        viewModelScope.launch {
            historyGroups.value = getTrainingHistoryUseCase()
        }
    }
}
