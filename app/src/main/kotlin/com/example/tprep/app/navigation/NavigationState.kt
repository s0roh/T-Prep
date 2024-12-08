package com.example.tprep.app.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tprep.app.navigation.Screen.DeckDetails.Companion.fromRoute

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

    fun <T : Any> navigateWithSaveState(route: T) {
        navHostController.navigate(route) {
            popUpTo(navHostController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun <T : Any> navigateWithLocalDecksRefresh(route: T) {
        navHostController.popBackStack()
        navHostController.navigate(Screen.LocalDecks)
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

fun navigateToRoute(route: String?, navController: NavHostController) {
    route?.let {
        fromRoute(it)?.let { screen ->
            navController.navigate(screen) {
                popUpTo(Screen.PublicDecks)
                launchSingleTop = true
            }
        } ?: Log.e("Navigation", "Unknown route: $it")
    } ?: Log.e("Navigation", "No route found in intent")
}

@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
): NavigationState {
    return remember {
        NavigationState(navHostController)
    }
}