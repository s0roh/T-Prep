package com.example.auth.domain.usecase

import com.example.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for refreshing the authentication state.
 *
 * This use case encapsulates the logic for refreshing the user's authentication
 * state by interacting with the [AuthRepository].
 *
 * @property repository The [AuthRepository] used to refresh the authentication state.
 */
internal class RefreshAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() = repository.refreshAuthState()
}