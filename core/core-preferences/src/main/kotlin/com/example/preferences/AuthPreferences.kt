package com.example.preferences

interface AuthPreferences {

    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpirationDate: String,
        refreshTokenExpirationDate: String
    )

    fun saveUserId(userId: String)

    fun getAccessToken(): String?
    fun getRefreshToken(): String?

    fun getAccessTokenExpirationDate(): String?
    fun getRefreshTokenExpirationDate(): String?

    fun getUserId(): String?

    fun clearTokens()

    fun isAccessTokenValid(): Boolean
    fun isRefreshTokenValid(): Boolean
}