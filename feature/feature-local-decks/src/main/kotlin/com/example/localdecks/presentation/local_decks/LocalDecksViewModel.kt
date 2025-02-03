package com.example.localdecks.presentation.local_decks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.usecase.GetDecksFlowUseCase
import com.example.localdecks.util.startSyncWork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LocalDecksViewModel @Inject constructor(
    getDecksFlowUseCase: GetDecksFlowUseCase,
) : ViewModel() {

    var isRefreshing = MutableStateFlow<Boolean>(false)
        private set

    val decks: StateFlow<List<Deck>> = getDecksFlowUseCase().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun startSync(context: Context) {
        viewModelScope.launch {
            isRefreshing.value = true

            val workId = startSyncWork(context)
            val workManager = WorkManager.getInstance(context)

            workManager.getWorkInfoByIdFlow(workId)
                .collect { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED || workInfo?.state == WorkInfo.State.FAILED) {
                        isRefreshing.value = false
                    }
                }
        }
    }
}