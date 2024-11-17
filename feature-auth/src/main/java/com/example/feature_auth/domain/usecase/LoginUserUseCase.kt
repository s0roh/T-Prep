package com.example.feature_auth.domain.usecase

import com.example.feature_auth.domain.entity.AuthState
import com.example.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

internal class LoginUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(userName: String, password: String): AuthState =
        repository.loginUser(userName, password)
}