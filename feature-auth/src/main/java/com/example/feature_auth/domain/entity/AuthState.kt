package com.example.feature_auth.domain.entity

sealed class AuthState {

    data class Authorized(val token: String) : AuthState()

    data object NotAuthorized : AuthState()

    data object Initial : AuthState()
}