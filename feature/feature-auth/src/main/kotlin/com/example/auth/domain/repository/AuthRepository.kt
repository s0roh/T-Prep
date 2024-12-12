package com.example.auth.domain.repository

interface AuthRepository {

    fun isAccessTokenValid(): Boolean

    fun isRefreshTokenValid(): Boolean

    suspend fun refreshTokens(): Boolean

    suspend fun login(email: String, password: String): Boolean

    suspend fun signup(email: String, password: String, name: String): Boolean
}