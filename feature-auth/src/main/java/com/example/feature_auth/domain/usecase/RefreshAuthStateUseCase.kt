package com.example.feature_auth.domain.usecase

import com.example.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

internal class RefreshAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() = repository.refreshAuthState()
}