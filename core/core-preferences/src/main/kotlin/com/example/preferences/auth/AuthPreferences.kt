package com.example.preferences.auth

interface AuthPreferences {

    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpirationDate: String,
        refreshTokenExpirationDate: String,
    )

    fun saveUserId(userId: String)
    fun saveUserName(username: String)
    fun saveUserEmail(email: String)
    fun saveUserProfileImage(uri: String)

    fun getAccessToken(): String?
    fun getRefreshToken(): String?

    fun getAccessTokenExpirationDate(): String?
    fun getRefreshTokenExpirationDate(): String?

    fun getUserId(): String?
    fun getUserName(): String?
    fun getUserEmail(): String?
    fun getUserProfileImage(): String?

    fun isVibrationEnabled(): Boolean
    fun isSoundEnabled(): Boolean
    fun toggleVibration()
    fun toggleSound()

    fun deleteUserProfileImage()

    fun clearTokens()

    fun isAccessTokenValid(): Boolean
    fun isRefreshTokenValid(): Boolean
}