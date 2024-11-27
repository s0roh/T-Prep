package com.example.auth.domain.usecase

import com.example.preferences.AuthPreferences
import javax.inject.Inject

/**
 * Use case for checking if the authentication token is valid.
 *
 * This use case encapsulates the logic for checking if the current authentication
 * token, stored in [AuthPreferences], is valid.
 *
 * @property preferences The [AuthPreferences] used to check the validity of the token.
 */
internal class IsTokenValidUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke(): Boolean = preferences.isTokenValid()
}