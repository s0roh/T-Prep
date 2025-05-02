package com.example.tprep.app.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tprep.app.navigation.NavigationState
import com.example.tprep.app.navigation.Screen

fun shouldShowBottomNavigation(currentRoute: String?): Boolean {
    return when {
        currentRoute?.contains("PublicDecks") == true -> true
        currentRoute == Screen.LocalDecks::class.qualifiedName -> true
        currentRoute == Screen.History::class.qualifiedName -> true
        currentRoute == Screen.Profile::class.qualifiedName -> true
        else -> false
    }
}

@Composable
fun currentRoute(navigationState: NavigationState): String? {
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}