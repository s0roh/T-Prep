package com.example.feature.history.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.history.domain.usecase.GetTrainingHistoryUseCase
import com.example.history.domain.entity.TrainingHistoryItem
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
