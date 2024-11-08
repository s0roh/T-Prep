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

    fun onLoginClick(login: String, password: String) {
        viewModelScope.launch(exceptionHandler) {
            if (login.isNotBlank() && password.isNotBlank()) {
                screenState.emit(LoginScreenState.Loading)
                val response = apiService.loginUser(
                    userName = login,
                    password = password
                )
                if (response.isSuccessful) {
                    screenState.emit(LoginScreenState.Success(response.body().toString()))
                } else {
                    throw Exception("Неверный логин или параль")
                }
            } else {
                throw Exception("Поля логин и пароль должны быть заполнены")
            }
        }
    }
}