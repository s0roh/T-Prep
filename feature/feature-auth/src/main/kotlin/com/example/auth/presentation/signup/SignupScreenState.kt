package com.example.auth.presentation.signup

internal sealed interface SignupScreenState {

    data object Initial : SignupScreenState

    data object Loading : SignupScreenState

    data class Error(val message: String) : SignupScreenState

    data class Success(val token: String) : SignupScreenState
}