package com.example.auth.domain.usecase

import com.example.preferences.AuthPreferences
import javax.inject.Inject

/**
 * Use case for retrieving the authentication token from preferences.
 *
 * This use case encapsulates the logic for retrieving the authentication token from
 * the [AuthPreferences] storage. The token is typically used for making authenticated
 * requests to the backend.
 *
 * @property preferences The [AuthPreferences] used for retrieving the authentication token.
 */
internal class GetTokenUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke(): String? = preferences.getToken()
}