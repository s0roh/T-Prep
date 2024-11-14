package com.example.core_preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class AuthPreferencesImpl(context: Context) : AuthPreferences {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveToken(token: String, expirationDate: String) {
        prefs.edit()
            .putString(TOKEN_KEY, token)
            .putString(EXPIRATION_KEY, expirationDate)
            .apply()
    }

    override fun getToken(): String? = prefs.getString(TOKEN_KEY, null)

    override fun getExpirationDate(): String? = prefs.getString(EXPIRATION_KEY, null)

    override fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).remove(EXPIRATION_KEY).apply()
    }

    @SuppressLint("NewApi")
    override fun isTokenValid(): Boolean {
        val expiration = getExpirationDate() ?: return false
        val expirationDate =
            OffsetDateTime.parse(expiration, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return OffsetDateTime.now().isBefore(expirationDate)
    }

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val TOKEN_KEY = "auth_token"
        private const val EXPIRATION_KEY = "token_expiration"
    }
}