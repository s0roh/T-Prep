package com.example.history.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.history.domain.entity.HistoryWithTimePeriod
import com.example.history.domain.usecase.GetGroupedHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    private val getGroupedHistoryUseCase: GetGroupedHistoryUseCase
) : ViewModel() {

    var historyGroups: MutableStateFlow<List<HistoryWithTimePeriod>> = MutableStateFlow(emptyList())
        private set

    fun loadHistory() {
        viewModelScope.launch {
            historyGroups.value = getGroupedHistoryUseCase()
        }
    }
}
