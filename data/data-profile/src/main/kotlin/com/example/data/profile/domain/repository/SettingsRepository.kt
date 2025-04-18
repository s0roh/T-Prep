package com.example.data.profile.domain.repository

interface SettingsRepository {

    fun isVibrationEnabled(): Boolean

    fun isSoundEnabled(): Boolean

    fun toggleVibration()

    fun toggleSound()
}