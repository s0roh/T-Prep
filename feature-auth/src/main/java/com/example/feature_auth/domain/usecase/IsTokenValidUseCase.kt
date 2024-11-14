package com.example.feature_auth.domain.usecase

import com.example.core_preferences.AuthPreferences

internal class IsTokenValidUseCase(
    private val preferences: AuthPreferences
) {

    operator fun invoke(): Boolean = preferences.isTokenValid()
}