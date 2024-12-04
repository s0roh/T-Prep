package com.example.tprep.app.navigation

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
    deckDetailsScreenContent: @Composable (Long, Source) -> Unit,
    trainingScreenContent: @Composable (Long, Source) -> Unit,
    historyScreenContent: @Composable () -> Unit,
    localDecksScreenContent: @Composable () -> Unit,
    addEditDeckScreenContent: @Composable (Long?) -> Unit,
    addEditCardScreenContent: @Composable (Long, Long?) -> Unit
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
            deckDetailsScreenContent(deck.deckId, deck.source)
        }
        composable<Screen.Training> { backStackEntry ->
            val training: Screen.Training = backStackEntry.toRoute()
            trainingScreenContent(training.deckId, training.source)
        }
        composable<Screen.History> {
            historyScreenContent()
        }
        composable<Screen.LocalDecks> {
            localDecksScreenContent()
        }
        composable<Screen.AddEditDeck> { backStackEntry ->
            val deckId: Screen.AddEditDeck = backStackEntry.toRoute()
            addEditDeckScreenContent(deckId.deckId)
        }
        composable<Screen.AddEditCard> { backStackEntry ->
            val screen: Screen.AddEditCard = backStackEntry.toRoute()
            addEditCardScreenContent(screen.deckId, screen.cardId)
        }
    }
}