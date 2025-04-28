package com.example.feature.profile.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.profile.domain.IsSoundEnabledUseCase
import com.example.feature.profile.domain.IsVibrationEnabledUseCase
import com.example.feature.profile.domain.ToggleSoundUseCase
import com.example.feature.profile.domain.ToggleVibrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val isVibrationEnabledUseCase: IsVibrationEnabledUseCase,
    private val isSoundEnabledUseCase: IsSoundEnabledUseCase,
    private val toggleVibrationUseCase: ToggleVibrationUseCase,
    private val toggleSoundUseCase: ToggleSoundUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<SettingsScreenState>(SettingsScreenState())
        private set

    var uiEvent = MutableSharedFlow<SettingsUiEvent>()
        private set

    init {
        viewModelScope.launch {
            val isVibrationEnabled = isVibrationEnabledUseCase()
            val isSoundEnabled = isSoundEnabledUseCase()
            screenState.value = SettingsScreenState(
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled
            )
        }
    }

    fun onVibrationToggle() {
        viewModelScope.launch {
            toggleVibrationUseCase()
            val updated = isVibrationEnabledUseCase()
            screenState.update { it.copy(isVibrationEnabled = updated) }

            if (updated) {
                uiEvent.emit(SettingsUiEvent.PlayVibration)
            }
        }
    }

    fun onSoundToggle() {
        viewModelScope.launch {
            toggleSoundUseCase()
            val updated = isSoundEnabledUseCase()
            screenState.update { it.copy(isSoundEnabled = updated) }

            if (updated) {
                uiEvent.emit(SettingsUiEvent.PlaySound)
            }
        }
    }
}