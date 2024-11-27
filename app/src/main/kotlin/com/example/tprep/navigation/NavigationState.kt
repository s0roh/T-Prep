package com.example.tprep.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {

    fun <T : Any> navigateTo(route: T) {
        navHostController.navigate(route) {
            popUpTo(Screen.PublicDecks) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true

        }
    }
    fun <T : Any> navigateToTraining(route: T) {
        navHostController.navigate(route) {
            popUpTo(navHostController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true

        }
    }


    fun <T : Any> navigateFromLogin(route: T) {
        navHostController.navigate(route) {
            popUpTo(Screen.Login) {
                saveState = true
                inclusive = true
            }
            restoreState = true
        }
    }
}


@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
): NavigationState {
    return remember {
        NavigationState(navHostController)
    }
}