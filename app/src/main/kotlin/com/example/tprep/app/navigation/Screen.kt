package com.example.tprep.app.navigation

import com.example.database.models.Source
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
    data object History : Screen

    @Serializable
    data object LocalDecks : Screen

    @Serializable
    data class AddEditDeck(
        val deckId: Long?
    )

    @Serializable
    data class AddEditCard(
        val deckId: Long,
        val cardId: Long?
    )

    @Serializable
    data class DeckDetails(
        val deckId: Long,
        val source: Source
    )

    @Serializable
    data class Training(
        val deckId: Long,
        val source: Source
    )
}