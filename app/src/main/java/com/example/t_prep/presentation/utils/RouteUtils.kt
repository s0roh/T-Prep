package com.example.t_prep.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.t_prep.navigation.NavigationState
import com.example.t_prep.navigation.Screen

fun shouldShowBottomNavigation(currentRoute: String?): Boolean {
    return currentRoute != Screen.Login::class.qualifiedName &&
            currentRoute?.startsWith(Screen.DeckDetails::class.qualifiedName!!) == false
}

@Composable
fun currentRoute(navigationState: NavigationState): String? {
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}