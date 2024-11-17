package com.example.feature_auth.presentation.login

internal sealed interface LoginScreenState {

    data object Initial : LoginScreenState

    data object Loading : LoginScreenState

    data class Error(val message: String) : LoginScreenState

    data class Success(val token: String) : LoginScreenState
}