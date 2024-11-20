package com.example.auth.domain.usecase

import com.example.preferences.AuthPreferences
import javax.inject.Inject

/**
 * Use case for saving the authentication token to preferences.
 *
 * This use case encapsulates the logic for storing the authentication token and its
 * expiration date in the [AuthPreferences].
 *
 * @property preferences The [AuthPreferences] used for saving the authentication token.
 */
internal class SaveTokenUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke(token: String, expirationDate: String) =
        preferences.saveToken(token, expirationDate)
}