package com.example.tprep.presentation.components

import com.example.tprep.R
import com.example.tprep.navigation.Screen
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItem<T>(val name: String, val icon: Int, val route: T) {
    data object PublicDecks: NavigationItem<Screen.PublicDecks>(
        name = "Public Decks",
        icon = R.drawable.ic_public_decks,
        route = Screen.PublicDecks
    )
    data object Profile: NavigationItem<Screen.Profile>(
        name = "Profile",
        icon = R.drawable.ic_profile,
        route = Screen.Profile
    )
}