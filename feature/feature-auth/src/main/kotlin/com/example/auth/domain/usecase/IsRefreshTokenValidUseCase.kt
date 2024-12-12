package com.example.auth.domain.usecase

import com.example.auth.domain.repository.AuthRepository
import javax.inject.Inject

internal class IsRefreshTokenValidUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    operator fun invoke(): Boolean = repository.isRefreshTokenValid()
}