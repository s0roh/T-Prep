package com.example.feature_auth.data.repository

import com.example.core_network.api.ApiService
import com.example.feature_auth.domain.entity.AuthState
import com.example.feature_auth.domain.repository.AuthRepository
import com.example.feature_auth.domain.usecase.GetTokenUseCase
import com.example.feature_auth.domain.usecase.IsTokenValidUseCase
import com.example.feature_auth.domain.usecase.SaveTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class AuthRepositoryImpl @Inject internal constructor(
    private val apiService: ApiService,
    private val getTokenUseCase: GetTokenUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : AuthRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val checkAuthStateEvents = MutableSharedFlow<Unit>(replay = 1)

    private val authStateFlow: StateFlow<AuthState> = flow {
        checkAuthStateEvents.emit(Unit)
        checkAuthStateEvents.collect {
            val currentToken = getTokenUseCase()
            val loggedIn = currentToken != null && isTokenValidUseCase()
            val authState =
                if (loggedIn) AuthState.Authorized(currentToken) else AuthState.NotAuthorized
            emit(authState)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = AuthState.Initial
    )

    override fun getAuthStateFlow(): StateFlow<AuthState> = authStateFlow

    override suspend fun refreshAuthState() {
        checkAuthStateEvents.emit(Unit)
    }

    override suspend fun loginUser(userName: String, password: String): AuthState {
        val response = apiService.loginUser(userName, password)
        return if (response.isSuccessful) {
            val token = response.body()
            val expiration = response.headers()[HEADER_EXPIRES_AFTER]
            if (token != null && expiration != null) {
                saveTokenUseCase(token, expiration)
                AuthState.Authorized(token)
            } else {
                AuthState.NotAuthorized
            }
        } else {
            throw IllegalStateException("Неверный логин или пароль")
        }
    }

    companion object {
        const val HEADER_EXPIRES_AFTER = "X-Expires-After"
    }
}