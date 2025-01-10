package com.example.tprep.app.presentation.components

import com.example.tprep.app.R
import com.example.tprep.app.navigation.Screen
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItem<T>(val name: String, val icon: Int, val route: T) {
    data object PublicDecks: NavigationItem<Screen.PublicDecks>(
        name = "Обзор",
        icon = R.drawable.ic_public,
        route = Screen.PublicDecks
    )
    data object LocalDecks: NavigationItem<Screen.LocalDecks>(
        name = "Колоды",
        icon = R.drawable.ic_local,
        route = Screen.LocalDecks
    )
    data object History: NavigationItem<Screen.History>(
        name = "История",
        icon = R.drawable.ic_history,
        route = Screen.History
    )
    data object Profile: NavigationItem<Screen.Profile>(
        name = "Профиль",
        icon = R.drawable.ic_profile,
        route = Screen.Profile
    )
}