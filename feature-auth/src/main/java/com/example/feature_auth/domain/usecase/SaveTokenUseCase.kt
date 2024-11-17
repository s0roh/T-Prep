package com.example.feature_auth.domain.usecase

import com.example.core_preferences.AuthPreferences
import javax.inject.Inject

internal class SaveTokenUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke(token: String, expirationDate: String) =
        preferences.saveToken(token, expirationDate)
}