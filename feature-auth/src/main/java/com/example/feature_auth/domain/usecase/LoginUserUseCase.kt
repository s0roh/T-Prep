package com.example.feature_auth.domain.usecase

import com.example.feature_auth.domain.entity.AuthState
import com.example.feature_auth.domain.repository.AuthRepository

internal class LoginUserUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(userName: String, password: String): AuthState =
        repository.loginUser(userName, password)
}