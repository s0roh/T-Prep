package com.example.auth.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.usecase.IsAccessTokenValidUseCase
import com.example.auth.domain.usecase.IsRefreshTokenValidUseCase
import com.example.auth.domain.usecase.LoginUseCase
import com.example.auth.domain.usecase.RefreshTokensUseCase
import com.example.auth.domain.usecase.SignupUseCase
import com.example.auth.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val isAccessTokenValidUseCase: IsAccessTokenValidUseCase,
    private val isRefreshTokenValidUseCase: IsRefreshTokenValidUseCase,
    private val refreshTokensUseCase: RefreshTokensUseCase,
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase
) : ViewModel() {

    var screenState = MutableStateFlow<AuthScreenState>(AuthScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        screenState.tryEmit(AuthScreenState.Error("Ошибка авторизации: ${exception.message}"))
    }

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch(exceptionHandler) {
            when {
                isAccessTokenValidUseCase() -> {
                    screenState.emit(AuthScreenState.Success("User is authenticated"))
                }
                isRefreshTokenValidUseCase() && refreshTokensUseCase() -> {
                    screenState.emit(AuthScreenState.Success("User is authenticated"))
                }
                else -> {
                    screenState.emit(AuthScreenState.Initial)
                }
            }
        }
    }

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            if (!isEmailValid(email)) {
                screenState.emit(AuthScreenState.Error("Введите корректную электронную почту"))
                return@launch
            }
            if (email.isNotBlank() && password.isNotBlank()) {
                screenState.emit(AuthScreenState.Loading)

                val loginSuccess = loginUseCase(email = email, password = password)
                Log.d("!@#", "$loginSuccess")
                if (loginSuccess) {
                    screenState.emit(AuthScreenState.Success("Login Successful"))
                } else {
                    screenState.emit(AuthScreenState.Error("Неверный логин или пароль"))
                }
            } else {
                screenState.emit(AuthScreenState.Error("Поля логин и пароль должны быть заполнены"))
            }
        }
    }

    fun onSignupClick(email: String, password: String, name: String) {
        viewModelScope.launch(exceptionHandler) {
            if (!isEmailValid(email)) {
                screenState.emit(AuthScreenState.Error("Введите корректную электронную почту"))
                return@launch
            }
            if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
                screenState.emit(AuthScreenState.Loading)

                val signupSuccess = signupUseCase(email = email, password = password, name = name)
                if (signupSuccess) {
                    screenState.emit(AuthScreenState.Success("Signup Successful"))
                } else {
                    screenState.emit(AuthScreenState.Error("Пользователь с такой электронной почтой уже существует"))
                }
            } else {
                screenState.emit(AuthScreenState.Error("Все поля должны быть заполнены"))
            }
        }
    }
}