package com.example.feature_auth.domain.repository

import com.example.feature_auth.domain.entity.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    fun getAuthStateFlow(): StateFlow<AuthState>

    suspend fun loginUser(userName: String, password: String): AuthState

    suspend fun refreshAuthState()
}