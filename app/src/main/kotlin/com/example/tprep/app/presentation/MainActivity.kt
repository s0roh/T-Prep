package com.example.tprep.app.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.auth.presentation.auth.AuthScreen
import com.example.database.models.Source
import com.example.decks.presentation.details.DeckDetailScreen
import com.example.decks.presentation.publicdecks.PublicDecksScreen
import com.example.feature.profile.presentation.ProfileScreen
import com.example.feature.reminder.presentation.add_reminder.AddReminderScreen
import com.example.feature.reminder.presentation.reminder.ReminderScreen
import com.example.history.presentation.history.HistoryScreen
import com.example.localdecks.presentation.add_edit_card.AddEditCardScreen
import com.example.localdecks.presentation.add_edit_deck.AddEditDeckScreen
import com.example.localdecks.presentation.local_decks.LocalDecksScreen
import com.example.localdecks.util.startSyncWork
import com.example.tprep.app.navigation.AppNavGraph
import com.example.tprep.app.navigation.Screen
import com.example.tprep.app.navigation.navigateToRoute
import com.example.tprep.app.navigation.rememberNavigationState
import com.example.tprep.app.presentation.components.AppBottomNavigation
import com.example.tprep.app.presentation.ui.theme.TPrepTheme
import com.example.tprep.app.presentation.utils.currentRoute
import com.example.tprep.app.presentation.utils.shouldShowBottomNavigation
import com.example.training.presentation.training.TrainingScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startSyncWork(this)
        enableEdgeToEdge()
        setContent {
            TPrepTheme {
                navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (::navController.isInitialized) {
            intent.getStringExtra("route")?.let { route ->
                navigateToRoute(route, navController)
            }
        } else {
            Log.e("Navigation", "NavController is not initialized")
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    val navigationState = rememberNavigationState(navHostController = navController)
    val currentRoute = currentRoute(navigationState)

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNavigation(currentRoute)) {
                AppBottomNavigation(navigationState = navigationState)
            }
        }
    ) { paddingValues ->

        val context = LocalContext.current
        LaunchedEffect(Unit) {
            (context as? Activity)?.intent?.getStringExtra("route")?.let { route ->
                // Необходима задержка для корректной работы с уведомлениями
                delay(100L)
                navigateToRoute(route, navController)
            }
        }

        AppNavGraph(
            navHostController = navigationState.navHostController,
            authScreenContent = {
                AuthScreen(
                    onSuccessAuthListener = {
                        navigationState.navigateFromLogin(Screen.PublicDecks)
                    }
                )
            },
            publicDecksScreenContent = {
                startSyncWork(context)
                PublicDecksScreen(
                    paddingValues = paddingValues,
                    onDeckClickListener = {
                        navigationState.navigateTo(Screen.DeckDetails(it, Source.NETWORK))
                    }
                )
            },
            historyScreenContent = {
                HistoryScreen(
                    paddingValues = paddingValues,
                    onHistoryClick = { deckId, source ->
                        navigationState.navigateWithSaveState(Screen.DeckDetails(deckId, source))
                    }
                )
            },
            profileScreenContent = {
                ProfileScreen(
                    paddingValues = paddingValues,
                    onLogoutClick = {
                        navigationState.navigateLogout(Screen.Auth)
                    }
                )
            },
            deckDetailsScreenContent = { deckId, source ->
                // TODO Заменить временное значение deckId = 2 на реальный идентификатор,
                // получаемый из параметра deckId. Сейчас используется константа для тестирования.
                var temporaryDeckId: String = deckId
                if (source == Source.NETWORK) temporaryDeckId =
                    "eff199f9-da03-4468-9f36-82e819ea516c"

                DeckDetailScreen(
                    deckId = temporaryDeckId,
                    source = source,
                    paddingValues = paddingValues,
                    onStartTraining = { deckId ->
                        navigationState.navigateWithSaveState(
                            Screen.Training(
                                deckId = deckId,
                                source = source
                            )
                        )
                    },
                    onDeleteDeck = { navigationState.navHostController.popBackStack() },
                    onEditDeck = { deckId ->
                        navigationState.navigateWithSaveState(Screen.AddEditDeck(deckId = deckId))
                    },
                    onEditCard = { deckId, cardId ->
                        navigationState.navigateWithSaveState(
                            Screen.AddEditCard(
                                deckId = deckId,
                                cardId = cardId
                            )
                        )
                    },
                    onAddCardClick = {
                        navigationState.navigateWithSaveState(
                            Screen.AddEditCard(
                                deckId = deckId,
                                cardId = null
                            )
                        )
                    },
                    // TODO поменять temporaryDeckId на deckId
                    onRemindClick = { deckName ->
                        navigationState.navigateWithSaveState(
                            Screen.Reminder(
                                deckId = temporaryDeckId,
                                source = source,
                                deckName = deckName
                            )
                        )
                    }
                )
            },
            trainingScreenContent = { deckId, source ->
                TrainingScreen(
                    paddingValues = paddingValues,
                    deckId = deckId,
                    source = source,
                    onFinishClick = { navigationState.navHostController.popBackStack() }
                )
            },
            localDecksScreenContent = {
                LocalDecksScreen(
                    onDeckClick = { deckId ->
                        navigationState.navigateWithSaveState(
                            Screen.DeckDetails(
                                deckId,
                                Source.LOCAL
                            )
                        )
                    },
                    onAddClick = {
                        navigationState.navigateWithSaveState(Screen.AddEditDeck(deckId = null))
                    }
                )
            },
            addEditDeckScreenContent = { deckId ->
                AddEditDeckScreen(
                    deckId = deckId,
                    onBackClick = { navigationState.navHostController.popBackStack() },
                    onSaveClick = { navigationState.navHostController.popBackStack() }
                )
            },
            addEditCardScreenContent = { deckId, cardId ->
                AddEditCardScreen(
                    cardId = cardId,
                    deckId = deckId,
                    onBackClick = { navigationState.navHostController.popBackStack() },
                    onSaveClick = { navigationState.navHostController.popBackStack() }
                )
            },
            reminderScreenContent = { deckId, deckName, source ->
                ReminderScreen(
                    deckId = deckId,
                    deckName = deckName,
                    source = source,
                    onAddClick = { deckId, deckName, source, isAutoGeneration ->
                        navigationState.navigateWithSaveState(
                            Screen.AddReminder(
                                deckId = deckId, deckName = deckName,
                                source = source, isAutoGeneration = isAutoGeneration
                            )
                        )
                    },
                    onBackClick = { navigationState.navHostController.popBackStack() }
                )
            },
            addReminderScreenContent = { deckId, deckName, source, isAutoGeneration ->
                AddReminderScreen(
                    deckId = deckId,
                    deckName = deckName,
                    source = source,
                    isAutoGeneration = isAutoGeneration,
                    onBackClick = { navigationState.navHostController.popBackStack() }
                )
            }
        )
    }
}