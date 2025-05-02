package com.example.tprep.app.presentation.components

import com.example.tprep.app.R
import com.example.tprep.app.navigation.Screen
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItem<T>(val nameResId: Int, val icon: Int, val route: T) {
    data object PublicDecks: NavigationItem<Screen.PublicDecks>(
        nameResId = R.string.review,
        icon = R.drawable.ic_public,
        route = Screen.PublicDecks()
    )
    data object LocalDecks: NavigationItem<Screen.LocalDecks>(
        nameResId = R.string.decks,
        icon = R.drawable.ic_local,
        route = Screen.LocalDecks
    )
    data object History: NavigationItem<Screen.History>(
        nameResId = R.string.history,
        icon = R.drawable.ic_history,
        route = Screen.History
    )
    data object Profile: NavigationItem<Screen.Profile>(
        nameResId = R.string.profile,
        icon = R.drawable.ic_profile,
        route = Screen.Profile
    )
}