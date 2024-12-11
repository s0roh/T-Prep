package com.example.tprep.app.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tprep.app.navigation.NavigationState
import com.example.tprep.app.navigation.Screen

fun shouldShowBottomNavigation(currentRoute: String?): Boolean {
    val shouldShowList = listOf(
        Screen.PublicDecks::class.qualifiedName,
        Screen.LocalDecks::class.qualifiedName,
        Screen.History::class.qualifiedName,
        Screen.Profile::class.qualifiedName
    )
    return currentRoute in shouldShowList
}

@Composable
fun currentRoute(navigationState: NavigationState): String? {
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}