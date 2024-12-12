package com.example.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AuthPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context
) : AuthPreferences {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpirationDate: String,
        refreshTokenExpirationDate: String
    ) {
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, accessToken)
            .putString(REFRESH_TOKEN_KEY, refreshToken)
            .putString(ACCESS_TOKEN_EXPIRATION_KEY, accessTokenExpirationDate)
            .putString(REFRESH_TOKEN_EXPIRATION_KEY, refreshTokenExpirationDate)
            .apply()
    }

    override fun getAccessToken(): String? = prefs.getString(ACCESS_TOKEN_KEY, null)

    override fun getRefreshToken(): String? = prefs.getString(REFRESH_TOKEN_KEY, null)

    override fun getAccessTokenExpirationDate(): String? = prefs.getString(ACCESS_TOKEN_EXPIRATION_KEY, null)

    override fun getRefreshTokenExpirationDate(): String? = prefs.getString(REFRESH_TOKEN_EXPIRATION_KEY, null)

    override fun clearTokens() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .remove(REFRESH_TOKEN_KEY)
            .remove(ACCESS_TOKEN_EXPIRATION_KEY)
            .remove(REFRESH_TOKEN_EXPIRATION_KEY)
            .apply()
    }

    override fun isAccessTokenValid(): Boolean {
        val expiration = getAccessTokenExpirationDate() ?: return false
        return isTokenValid(expiration)
    }

    override fun isRefreshTokenValid(): Boolean {
        val expiration = getRefreshTokenExpirationDate() ?: return false
        return isTokenValid(expiration)
    }

    private fun isTokenValid(expiration: String): Boolean {
        return try {
            val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val expirationDate = format.parse(expiration)
            expirationDate != null && Date().before(expirationDate)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    companion object {

        private const val PREFS_NAME = "auth_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val ACCESS_TOKEN_EXPIRATION_KEY = "access_token_expiration"
        private const val REFRESH_TOKEN_EXPIRATION_KEY = "refresh_token_expiration"
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSSSSS Z 'UTC'"
    }
}