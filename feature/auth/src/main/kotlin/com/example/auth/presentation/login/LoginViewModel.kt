package com.example.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.entity.AuthState
import com.example.auth.domain.usecase.GetAuthStateFlowUseCase
import com.example.auth.domain.usecase.LoginUserUseCase
import com.example.auth.domain.usecase.RefreshAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val getAuthStateFlowUseCase: GetAuthStateFlowUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val refreshAuthStateUseCase: RefreshAuthStateUseCase,
) : ViewModel() {

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