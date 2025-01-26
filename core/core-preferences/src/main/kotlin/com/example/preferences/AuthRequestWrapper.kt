package com.example.preferences

import javax.inject.Inject

class AuthRequestWrapper @Inject constructor(
    private val authPreferences: AuthPreferences,
) {
    suspend fun <T> executeWithAuth(
        call: suspend (authHeader: String?) -> T,
    ): T {
        val accessToken = authPreferences.getAccessToken()
        val authHeader = if (accessToken != null && authPreferences.isAccessTokenValid()) {
            "Bearer $accessToken"
        } else null
        return call(authHeader)
    }
}
