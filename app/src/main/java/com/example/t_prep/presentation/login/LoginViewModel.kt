package com.example.t_prep.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t_prep.data.network.api.ApiFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val apiService = ApiFactory.apiService

    private val _loginFlow = MutableSharedFlow<LoginScreenState>()

    val screenState: StateFlow<LoginScreenState> = flow {
        emitAll(_loginFlow)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoginScreenState.Initial
    )


    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _loginFlow.tryEmit(LoginScreenState.Error("Ошибка авторизации: ${exception.message}"))
    }


    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            if (email.isNotBlank() && password.isNotBlank()) {
                _loginFlow.emit(LoginScreenState.Loading)
                val token = apiService.loginUser(
                    userName = email,
                    password = password
                )
                if (token.isSuccessful) {
                    _loginFlow.emit(LoginScreenState.Success(token.body().toString()))
                } else {
                    _loginFlow.emit(LoginScreenState.Error("Ошибка авторизации: неверный логин или параль"))
                }

            } else {
                _loginFlow.emit(LoginScreenState.Error("Ошибка авторизации: поля логин и пароль должны быть заполнены"))
            }
        }
    }
}