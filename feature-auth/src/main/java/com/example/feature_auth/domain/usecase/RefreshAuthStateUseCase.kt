package com.example.feature_auth.domain.usecase

import com.example.feature_auth.domain.repository.AuthRepository

internal class RefreshAuthStateUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() = repository.refreshAuthState()
}