package com.example.tprep.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tprep.navigation.NavigationState
import com.example.tprep.navigation.Screen

fun shouldShowBottomNavigation(currentRoute: String?): Boolean {
    return currentRoute != Screen.Login::class.qualifiedName &&
            currentRoute?.startsWith(Screen.DeckDetails::class.qualifiedName!!) == false &&
            currentRoute.startsWith(Screen.Training::class.qualifiedName!!) == false &&
            currentRoute.startsWith(Screen.AddEditCard::class.qualifiedName!!) == false &&
            currentRoute.startsWith(Screen.AddEditDeck::class.qualifiedName!!) == false
}

@Composable
fun currentRoute(navigationState: NavigationState): String? {
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}