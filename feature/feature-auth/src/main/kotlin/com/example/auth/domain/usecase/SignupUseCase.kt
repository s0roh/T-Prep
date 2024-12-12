package com.example.auth.domain.usecase

import com.example.auth.domain.repository.AuthRepository
import javax.inject.Inject

internal class SignupUseCase @Inject constructor(
    private val repository: AuthRepository,
) {

    suspend operator fun invoke(email: String, password: String, name: String): Boolean =
        repository.signup(email = email, password = password, name = name)
}