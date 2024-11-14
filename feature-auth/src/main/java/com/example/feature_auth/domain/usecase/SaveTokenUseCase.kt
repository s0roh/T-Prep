package com.example.feature_auth.domain.usecase

import com.example.core_preferences.AuthPreferences

internal class SaveTokenUseCase(
    private val preferences: AuthPreferences
) {

    operator fun invoke(token: String, expirationDate: String) =
        preferences.saveToken(token, expirationDate)
}