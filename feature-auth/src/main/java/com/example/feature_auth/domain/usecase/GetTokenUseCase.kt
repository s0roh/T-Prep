package com.example.feature_auth.domain.usecase

import com.example.core_preferences.AuthPreferences
import javax.inject.Inject

internal class GetTokenUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke(): String? = preferences.getToken()
}