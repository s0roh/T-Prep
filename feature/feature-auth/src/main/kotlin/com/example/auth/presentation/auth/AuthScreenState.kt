package com.example.auth.presentation.auth

internal sealed interface AuthScreenState {

    data object Initial : AuthScreenState

    data object Loading : AuthScreenState

    data class Error(val message: String) : AuthScreenState

    data class Success(val token: String) : AuthScreenState
}