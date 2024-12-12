package com.example.auth.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.IsAccessTokenValidUseCase
import com.example.auth.domain.usecase.IsRefreshTokenValidUseCase
import com.example.auth.domain.usecase.LoginUseCase
import com.example.auth.domain.usecase.RefreshTokensUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val isAccessTokenValidUseCase: IsAccessTokenValidUseCase,
    private val isRefreshTokenValidUseCase: IsRefreshTokenValidUseCase,
    private val refreshTokensUseCase: RefreshTokensUseCase,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<LoginScreenState>(LoginScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        screenState.tryEmit(LoginScreenState.Error("Ошибка авторизации: ${exception.message}"))
    }

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch(exceptionHandler) {
            when {
                isAccessTokenValidUseCase() -> {
                    screenState.emit(LoginScreenState.Success("User is authenticated"))
                }
                isRefreshTokenValidUseCase() && refreshTokensUseCase() -> {
                    screenState.emit(LoginScreenState.Success("User is authenticated"))
                }
                else -> {
                    screenState.emit(LoginScreenState.Initial)
                }
            }
        }
    }

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            if (email.isNotBlank() && password.isNotBlank()) {
                screenState.emit(LoginScreenState.Loading)

                val loginSuccess = loginUseCase(email = email, password = password)
                Log.d("!@#", "$loginSuccess")
                if (loginSuccess) {
                    screenState.emit(LoginScreenState.Success("Login Successful"))
                } else {
                    screenState.emit(LoginScreenState.Error("Неверный логин или пароль"))
                }
            } else {
                screenState.emit(LoginScreenState.Error("Поля логин и пароль должны быть заполнены"))
            }
        }
    }
}