package com.example.core_preferences

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

    override fun isTokenValid(): Boolean {
        val expiration = getExpirationDate() ?: return false
        return try {
            val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val expirationDate = format.parse(expiration)
            expirationDate != null && Date().before(expirationDate)
        } catch (_: Exception) {
            false
        }
    }

    companion object {

        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        private const val PREFS_NAME = "auth_prefs"
        private const val TOKEN_KEY = "auth_token"
        private const val EXPIRATION_KEY = "token_expiration"
    }
}