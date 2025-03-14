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
    authScreenContent: @Composable () -> Unit,
    publicDecksScreenContent: @Composable () -> Unit,
    profileScreenContent: @Composable () -> Unit,
    deckDetailsScreenContent: @Composable (String, Source) -> Unit,
    deckDetailsStatisticScreenContent: @Composable (String) -> Unit,
    trainingScreenContent: @Composable (String, Source) -> Unit,
    trainingResultsScreenContent: @Composable (String, Boolean) -> Unit,
    trainingErrorsScreenContent: @Composable (String) -> Unit,
    trainingModeSettingsScreenContent: @Composable (String) -> Unit,
    historyScreenContent: @Composable () -> Unit,
    localDecksScreenContent: @Composable () -> Unit,
    addEditDeckScreenContent: @Composable (String?) -> Unit,
    addEditCardScreenContent: @Composable (String, Int?) -> Unit,
    reminderScreenContent: @Composable (String, String, Source) -> Unit,
    addReminderScreenContent: @Composable (String, String, Source, Boolean) -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Auth
    ) {
        composable<Screen.Auth> {
            authScreenContent()
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
        composable<Screen.DeckDetailsStatistic> { backStackEntry ->
            val statistic: Screen.DeckDetailsStatistic = backStackEntry.toRoute()
            deckDetailsStatisticScreenContent(statistic.deckId)
        }
        composable<Screen.Training> { backStackEntry ->
            val training: Screen.Training = backStackEntry.toRoute()
            trainingScreenContent(training.deckId, training.source)
        }
        composable<Screen.TrainingResults> { backStackEntry ->
            val trainingResults: Screen.TrainingResults = backStackEntry.toRoute()
            trainingResultsScreenContent(
                trainingResults.trainingSessionId,
                trainingResults.cameFromHistoryScreen
            )
        }
        composable<Screen.TrainingErrors> { backStackEntry ->
            val trainingErrors: Screen.TrainingErrors = backStackEntry.toRoute()
            trainingErrorsScreenContent(trainingErrors.trainingSessionId)
        }
        composable<Screen.TrainingModeSettings> { backStackEntry ->
            val trainingModeSettings: Screen.TrainingModeSettings = backStackEntry.toRoute()
            trainingModeSettingsScreenContent(trainingModeSettings.deckId)
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
        composable<Screen.Reminder> { backStackEntry ->
            val screen: Screen.Reminder = backStackEntry.toRoute()
            reminderScreenContent(screen.deckId, screen.deckName, screen.source)
        }
        composable<Screen.AddReminder> { backStackEntry ->
            val screen: Screen.AddReminder = backStackEntry.toRoute()
            addReminderScreenContent(
                screen.deckId,
                screen.deckName,
                screen.source,
                screen.isAutoGeneration
            )
        }
    }
}