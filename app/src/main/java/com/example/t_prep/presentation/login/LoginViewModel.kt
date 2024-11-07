package com.example.t_prep.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t_prep.data.network.api.ApiFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val apiService = ApiFactory.apiService

    var screenState = MutableStateFlow<LoginScreenState>(LoginScreenState.Initial)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        screenState.tryEmit(LoginScreenState.Error("Ошибка авторизации: ${exception.message}"))
    }

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            if (email.isNotBlank() && password.isNotBlank()) {
                screenState.emit(LoginScreenState.Loading)
                val token = apiService.loginUser(
                    userName = email,
                    password = password
                )
                if (token.isSuccessful) {
                    screenState.emit(LoginScreenState.Success(token.body().toString()))
                } else {
                    screenState.emit(LoginScreenState.Error("Ошибка авторизации: неверный логин или параль"))
                }

            } else {
                screenState.emit(LoginScreenState.Error("Ошибка авторизации: поля логин и пароль должны быть заполнены"))
            }
        }
    }
}