package com.example.preferences.auth.util

import com.example.network.api.ApiService
import com.example.network.dto.user.RefreshRequestDto
import com.example.preferences.auth.AuthPreferences
import javax.inject.Inject

class AuthRequestWrapper @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val apiService: ApiService,
) {
    suspend fun <T> executeWithAuth(
        call: suspend (authHeader: String?) -> T,
    ): T {
        var accessToken = authPreferences.getAccessToken()
        val authHeader = if (accessToken != null && authPreferences.isAccessTokenValid()) {
            "Bearer $accessToken"
        } else {
            val refreshed = refreshTokens()
            accessToken = authPreferences.getAccessToken()
            if (refreshed && accessToken != null && authPreferences.isAccessTokenValid()) {
                "Bearer $accessToken"
            } else {
                null
            }
        }

        return call(authHeader)
    }

    private suspend fun refreshTokens(): Boolean {
        val refreshToken = authPreferences.getRefreshToken()?.takeIf {
            authPreferences.isRefreshTokenValid()
        } ?: return false

        val response = apiService.refreshToken(RefreshRequestDto(refreshToken = refreshToken))
        if (!response.isSuccessful) return false

        val newAccessToken = response.body()?.accessToken
        val newRefreshToken = response.body()?.refreshToken
        val accessTokenExpirationDate = response.headers()["X-Access-Expires-After"] ?: ""
        val refreshTokenExpirationDate = response.headers()["X-Refresh-Expires-After"] ?: ""

        return if (newAccessToken != null && newRefreshToken != null) {
            authPreferences.saveTokens(
                newAccessToken,
                newRefreshToken,
                accessTokenExpirationDate,
                refreshTokenExpirationDate
            )
            true
        } else {
            false
        }
    }
}