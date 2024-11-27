package com.example.auth.domain.repository

import com.example.auth.domain.entity.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    fun getAuthStateFlow(): StateFlow<AuthState>

    suspend fun loginUser(userName: String, password: String): AuthState

    suspend fun refreshAuthState()
}