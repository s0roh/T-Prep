package com.example.auth.domain.usecase

import com.example.auth.domain.repository.AuthRepository
import javax.inject.Inject

internal class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String): Boolean =
        repository.login(email = email, password = password)
}