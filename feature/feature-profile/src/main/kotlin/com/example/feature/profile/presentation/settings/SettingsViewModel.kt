package com.example.feature.profile.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.imageLoader
import com.example.feature.profile.domain.ClearTokensUseCase
import com.example.feature.profile.domain.IsSoundEnabledUseCase
import com.example.feature.profile.domain.IsVibrationEnabledUseCase
import com.example.feature.profile.domain.ToggleSoundUseCase
import com.example.feature.profile.domain.ToggleVibrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val isVibrationEnabledUseCase: IsVibrationEnabledUseCase,
    private val isSoundEnabledUseCase: IsSoundEnabledUseCase,
    private val toggleVibrationUseCase: ToggleVibrationUseCase,
    private val toggleSoundUseCase: ToggleSoundUseCase,
    private val clearTokensUseCase: ClearTokensUseCase,
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

    fun logout() {
        clearTokensUseCase()
        File(appContext.filesDir, "profile_pic.jpg").delete()
        appContext.imageLoader.memoryCache?.clear()
    }
}