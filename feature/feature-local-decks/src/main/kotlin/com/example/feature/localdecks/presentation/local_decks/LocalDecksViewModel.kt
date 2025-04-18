package com.example.feature.localdecks.presentation.local_decks

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.common.ui.entity.DeckUiModel
import com.example.common.ui.snackbar_controller.SnackbarAction
import com.example.common.ui.snackbar_controller.SnackbarController
import com.example.common.ui.snackbar_controller.SnackbarEvent
import com.example.feature.localdecks.domain.usecase.DeleteDeckUseCase
import com.example.feature.localdecks.domain.usecase.GetDecksFlowUseCase
import com.example.feature.localdecks.domain.usecase.RestoreDeckUseCase
import com.example.feature.localdecks.domain.usecase.SoftDeleteDeckUseCase
import com.example.localdecks.util.startSyncWork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
internal class LocalDecksViewModel @Inject constructor(
    getDecksFlowUseCase: GetDecksFlowUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val softDeleteDeckUseCase: SoftDeleteDeckUseCase,
    private val restoreDeckUseCase: RestoreDeckUseCase,
) : ViewModel() {

    var isRefreshing = MutableStateFlow<Boolean>(false)
        private set

    val decks: StateFlow<List<DeckUiModel>> = getDecksFlowUseCase().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun startSync(context: Context) {
        viewModelScope.launch {
            isRefreshing.value = true

            val workId = startSyncWork(context)
            val workManager = WorkManager.getInstance(context)

            val workInfoFlow = workManager.getWorkInfoByIdFlow(workId)

            val workInfo = withTimeoutOrNull(LOAD_DECKS_TIMEOUT_MS) {
                workInfoFlow.firstOrNull { workInfo ->
                    workInfo?.state == WorkInfo.State.SUCCEEDED || workInfo?.state == WorkInfo.State.FAILED
                }
            }

            isRefreshing.value = false

            if (workInfo == null) {
                // Если WorkManager ничего не вернул за 10 сек — проблема с сетью
                Toast.makeText(
                    context,
                    "Ошибка синхронизации. Проверьте подключение к интернету",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (workInfo.state == WorkInfo.State.FAILED) {
                // Если вернул, но завершился с ошибкой
                Toast.makeText(context, "Ошибка синхронизации", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteDeckWithUndo(deckId: String, deckName: String) {
        viewModelScope.launch {
            softDeleteDeckUseCase(deckId = deckId)
            showSnackbar(deckId = deckId, deckName = deckName)
        }
    }

    private fun showSnackbar(deckId: String, deckName: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = "Колода \"$deckName\" удалена.",
                    action = SnackbarAction(
                        name = "Восстановить",
                        action = {
                            restoreDeckUseCase(deckId = deckId)
                        },
                        dismiss = {
                            deleteDeckUseCase(deckId = deckId)
                        }
                    )
                )
            )
        }
    }

    companion object {

        private const val LOAD_DECKS_TIMEOUT_MS: Long = 10_000L
    }
}