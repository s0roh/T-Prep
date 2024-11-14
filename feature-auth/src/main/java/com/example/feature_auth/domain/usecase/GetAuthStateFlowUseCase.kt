package com.example.feature_auth.domain.usecase

import com.example.feature_auth.domain.entity.AuthState
import com.example.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

internal class GetAuthStateFlowUseCase(
    private val repository: AuthRepository
) {

    operator fun invoke(): StateFlow<AuthState> {
        return repository.getAuthStateFlow()
    }
}