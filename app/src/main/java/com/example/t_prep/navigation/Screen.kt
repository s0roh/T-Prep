package com.example.t_prep.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Login : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object PublicDecks : Screen

    @Serializable
    data class DeckDetails(
        val deckId: Long
    )
}