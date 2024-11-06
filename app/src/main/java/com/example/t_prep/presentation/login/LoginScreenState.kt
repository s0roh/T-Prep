package com.example.t_prep.presentation.login

sealed interface LoginScreenState{

    data object Initial: LoginScreenState

    data object Loading: LoginScreenState

    data class Error(val message: String) : LoginScreenState

    data class Success(val token: String) : LoginScreenState
}