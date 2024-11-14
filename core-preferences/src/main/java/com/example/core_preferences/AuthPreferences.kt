package com.example.core_preferences

interface AuthPreferences {

    fun saveToken(token: String, expirationDate: String)

    fun getToken(): String?

    fun getExpirationDate(): String?

    fun clearToken()

    fun isTokenValid(): Boolean
}