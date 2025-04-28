package com.example.feature.profile.presentation.settings

sealed class SettingsUiEvent {

    data object PlayVibration : SettingsUiEvent()

    data object PlaySound : SettingsUiEvent()
}