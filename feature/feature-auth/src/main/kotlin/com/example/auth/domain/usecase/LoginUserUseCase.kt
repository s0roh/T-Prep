package com.example.auth.domain.usecase

import com.example.auth.domain.entity.AuthState
import com.example.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging in the user.
 *
 * This use case encapsulates the logic for authenticating the user with their username
 * and password via the [AuthRepository]. Upon successful login, it returns an [AuthState]
 * representing the user's authentication state.
 *
 * @property repository The [AuthRepository] used to handle the login process.
 */
internal class LoginUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(userName: String, password: String): AuthState =
        repository.loginUser(userName, password)
}