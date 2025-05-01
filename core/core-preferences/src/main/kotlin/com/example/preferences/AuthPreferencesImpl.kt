package com.example.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AuthPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context,
) : AuthPreferences {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExpirationDate: String,
        refreshTokenExpirationDate: String,
    ) {
        prefs.edit {
            putString(ACCESS_TOKEN_KEY, accessToken)
                .putString(REFRESH_TOKEN_KEY, refreshToken)
                .putString(ACCESS_TOKEN_EXPIRATION_KEY, accessTokenExpirationDate)
                .putString(REFRESH_TOKEN_EXPIRATION_KEY, refreshTokenExpirationDate)
        }
    }

    override fun saveUserId(userId: String) {
        prefs.edit { putString(USER_ID_KEY, userId) }
    }

    override fun saveUserName(username: String) {
        prefs.edit { putString(USER_NAME_KEY, username) }
    }

    override fun saveUserEmail(email: String) {
        prefs.edit { putString(USER_EMAIL_KEY, email) }
    }

    override fun saveUserProfileImage(uri: String) {
        prefs.edit { putString(USER_PROFILE_IMAGE_KEY, uri) }
    }

    override fun getAccessToken(): String? = prefs.getString(ACCESS_TOKEN_KEY, null)

    override fun getRefreshToken(): String? = prefs.getString(REFRESH_TOKEN_KEY, null)

    override fun getAccessTokenExpirationDate(): String? =
        prefs.getString(ACCESS_TOKEN_EXPIRATION_KEY, null)

    override fun getRefreshTokenExpirationDate(): String? =
        prefs.getString(REFRESH_TOKEN_EXPIRATION_KEY, null)

    override fun getUserId(): String? = prefs.getString(USER_ID_KEY, null)

    override fun getUserName(): String? = prefs.getString(USER_NAME_KEY, null)

    override fun getUserEmail(): String? = prefs.getString(USER_EMAIL_KEY, null)

    override fun getUserProfileImage(): String? = prefs.getString(USER_PROFILE_IMAGE_KEY, null)

    override fun isVibrationEnabled(): Boolean = prefs.getBoolean(VIBRATION_ENABLED_KEY, true)

    override fun isSoundEnabled(): Boolean = prefs.getBoolean(SOUND_ENABLED_KEY, true)

    override fun toggleVibration() {
        val current = isVibrationEnabled()
        setVibrationEnabled(!current)
    }

    override fun toggleSound() {
        val current = isSoundEnabled()
        setSoundEnabled(!current)
    }

    override fun deleteUserProfileImage() {
        prefs.edit {
            remove(USER_PROFILE_IMAGE_KEY)
        }
    }

    override fun clearTokens() {
        prefs.edit {
            remove(ACCESS_TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .remove(ACCESS_TOKEN_EXPIRATION_KEY)
                .remove(REFRESH_TOKEN_EXPIRATION_KEY)
                .remove(USER_ID_KEY)
                .remove(USER_NAME_KEY)
                .remove(USER_EMAIL_KEY)
                .remove(USER_PROFILE_IMAGE_KEY)
        }
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

    private fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(VIBRATION_ENABLED_KEY, enabled) }
    }

    private fun setSoundEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(SOUND_ENABLED_KEY, enabled) }
    }

    companion object {

        private const val PREFS_NAME = "auth_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val ACCESS_TOKEN_EXPIRATION_KEY = "access_token_expiration"
        private const val REFRESH_TOKEN_EXPIRATION_KEY = "refresh_token_expiration"
        private const val USER_ID_KEY = "user_id"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_PROFILE_IMAGE_KEY = "user_profile_image"
        private const val VIBRATION_ENABLED_KEY = "vibration_enabled"
        private const val SOUND_ENABLED_KEY = "sound_enabled"
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSSSSS Z 'UTC'"
    }
}