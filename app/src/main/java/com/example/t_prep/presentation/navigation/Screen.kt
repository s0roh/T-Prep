package com.example.t_prep.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Login : Screen

    @Serializable
    data class PublicDecks(
        val token: String
    ) : Screen
}