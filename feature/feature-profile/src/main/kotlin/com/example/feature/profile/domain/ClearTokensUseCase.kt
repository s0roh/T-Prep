package com.example.feature.profile.domain

import com.example.preferences.auth.AuthPreferences
import javax.inject.Inject

internal class ClearTokensUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke() = preferences.clearTokens()
}