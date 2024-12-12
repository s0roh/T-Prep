package com.example.tprep.app.navigation

import com.example.database.models.Source
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Auth : Screen

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
        val deckId: String?
    ) : Screen

    @Serializable
    data class AddEditCard(
        val deckId: String,
        val cardId: Int?
    ) : Screen

    @Serializable
    data class DeckDetails(
        val deckId: String,
        val source: Source
    ) : Screen {
        fun toRoute(): String {
            return "deckDetails/$deckId/${source.name}"
        }

        companion object {
            fun fromRoute(route: String): Screen? {
                val parts = route.split("/")
                val deckId = parts.getOrNull(1) ?: return null
                val source = parts.getOrNull(2)?.let { Source.valueOf(it) } ?: return null
                return DeckDetails(deckId, source)
            }
        }
    }

    @Serializable
    data class Training(
        val deckId: String,
        val source: Source
    ) : Screen

    @Serializable
    data class Reminder(
        val deckId: String,
        val source: Source,
        val deckName: String
    ) : Screen
}