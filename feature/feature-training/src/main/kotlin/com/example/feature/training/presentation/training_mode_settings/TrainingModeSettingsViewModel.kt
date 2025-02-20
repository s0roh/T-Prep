package com.example.feature.training.presentation.training_mode_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.training.domain.GetTrainingModesUseCase
import com.example.feature.training.domain.SaveTrainingModesUseCase
import com.example.training.domain.entity.TrainingModes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TrainingModeSettingsViewModel @Inject constructor(
    private val getTrainingModesUseCase: GetTrainingModesUseCase,
    private val saveTrainingModesUseCase: SaveTrainingModesUseCase
) : ViewModel() {

    private var trainingModesState: TrainingModes? = null

    suspend fun loadModeSettings(deckId: String): TrainingModes {
        val modes = getTrainingModesUseCase(deckId = deckId)
        trainingModesState = modes
        return modes
    }

    fun updateModes(modes: TrainingModes) {
        trainingModesState = modes
    }

    fun saveModeSettings() {
        trainingModesState?.let { modes ->
            viewModelScope.launch {
                saveTrainingModesUseCase(modes)
            }
        }
    }
}