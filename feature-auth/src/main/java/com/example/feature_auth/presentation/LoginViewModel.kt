package com.example.feature_auth.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_auth.data.repository.AuthRepositoryImpl
import com.example.feature_auth.domain.entity.AuthState
import com.example.feature_auth.domain.usecase.GetAuthStateFlowUseCase
import com.example.feature_auth.domain.usecase.LoginUserUseCase
import com.example.feature_auth.domain.usecase.RefreshAuthStateUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class LoginViewModel(context: Context) : ViewModel() {

    private val repository = AuthRepositoryImpl(context)
    private val getAuthStateFlowUseCase = GetAuthStateFlowUseCase(repository)
    private val loginUserUseCase = LoginUserUseCase(repository)
    private val refreshAuthStateUseCase = RefreshAuthStateUseCase(repository)

    var screenState = MutableStateFlow<LoginScreenState>(LoginScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        screenState.tryEmit(LoginScreenState.Error("Ошибка авторизации: ${exception.message}"))
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            getAuthStateFlowUseCase().collect { authState ->
                screenState.value = when (authState) {
                    is AuthState.Authorized -> LoginScreenState.Success(authState.token)
                    AuthState.Initial -> LoginScreenState.Initial
                    AuthState.NotAuthorized -> LoginScreenState.Initial
                }
            }
        }
    }

    fun onLoginClick(login: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            if (login.isNotBlank() && password.isNotBlank()) {
                screenState.emit(LoginScreenState.Loading)
                val authState = loginUserUseCase(login, password)
                if (authState is AuthState.Authorized) {
                    refreshAuthStateUseCase()
                }
            } else {
                screenState.value =
                    LoginScreenState.Error("Поля логин и пароль должны быть заполнены")
            }
        }
    }
}