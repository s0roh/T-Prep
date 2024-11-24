package com.example.tprep.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.database.models.Source

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    loginScreenContent: @Composable () -> Unit,
    publicDecksScreenContent: @Composable () -> Unit,
    profileScreenContent: @Composable () -> Unit,
    deckDetailsScreenContent: @Composable (Long) -> Unit,
    trainingScreenContent: @Composable (Long, Source) -> Unit
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
        composable<Screen.Training> { backStackEntry ->
            val training: Screen.Training = backStackEntry.toRoute()
            trainingScreenContent(training.deckId, training.source)
        }
    }
}