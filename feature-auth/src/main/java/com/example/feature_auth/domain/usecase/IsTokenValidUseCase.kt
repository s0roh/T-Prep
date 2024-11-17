package com.example.feature_auth.domain.usecase

import com.example.core_preferences.AuthPreferences
import javax.inject.Inject

internal class IsTokenValidUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke(): Boolean = preferences.isTokenValid()
}