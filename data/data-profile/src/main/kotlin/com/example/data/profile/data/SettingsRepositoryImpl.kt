package com.example.data.profile.data

import com.example.data.profile.domain.repository.SettingsRepository
import com.example.preferences.auth.AuthPreferences
import javax.inject.Inject

class SettingsRepositoryImpl @Inject internal constructor(
    private val preferences: AuthPreferences,
) : SettingsRepository {

    override fun isVibrationEnabled(): Boolean {
        return preferences.isVibrationEnabled()
    }

    override fun isSoundEnabled(): Boolean {
        return preferences.isSoundEnabled()
    }

    override fun toggleVibration() {
        preferences.toggleVibration()
    }

    override fun toggleSound() {
       preferences.toggleSound()
    }
}