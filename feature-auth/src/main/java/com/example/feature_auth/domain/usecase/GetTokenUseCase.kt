package com.example.feature_auth.domain.usecase

import com.example.core_preferences.AuthPreferences

internal class GetTokenUseCase(
    private val preferences: AuthPreferences
) {

    operator fun invoke(): String? = preferences.getToken()
}