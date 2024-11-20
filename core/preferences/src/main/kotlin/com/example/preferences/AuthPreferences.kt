package com.example.preferences

interface AuthPreferences {

    fun saveToken(token: String, expirationDate: String)

    fun getToken(): String?

    fun getExpirationDate(): String?

    fun clearToken()

    fun isTokenValid(): Boolean
}