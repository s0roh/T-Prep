package com.example.t_prep.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    loginScreenContent: @Composable () -> Unit,
    publicDecksScreenContent: @Composable () -> Unit,
    profileScreenContent: @Composable () -> Unit,
    deckDetailsScreenContent: @Composable (Long) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Login
    ) {
        composable<Screen.Login> {
            loginScreenContent()
        }
        composable<Screen.PublicDecks> {
            publicDecksScreenContent()
        }
        composable<Screen.Profile> {
            profileScreenContent()
        }
        composable<Screen.DeckDetails> { backStackEntry ->
            val deck: Screen.DeckDetails = backStackEntry.toRoute()
            deckDetailsScreenContent(deck.deckId)
        }
    }
}