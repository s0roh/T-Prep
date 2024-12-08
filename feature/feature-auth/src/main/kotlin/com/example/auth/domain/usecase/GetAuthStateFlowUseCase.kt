package com.example.auth.domain.usecase

import com.example.auth.domain.entity.AuthState
import com.example.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Use case for retrieving the authentication state as a flow.
 *
 * This use case encapsulates the logic for obtaining the current authentication state
 * from the [AuthRepository]. The state is returned as a [StateFlow], which allows
 * observing real-time changes to the authentication status.
 *
 * @property repository The [AuthRepository] used to get the authentication state.
 */
internal class GetAuthStateFlowUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    operator fun invoke(): StateFlow<AuthState> {
        return repository.getAuthStateFlow()
    }
}