package com.example.tprep.app.presentation.components

import android.app.Activity
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.auth.presentation.auth.AuthScreen
import com.example.common.ui.CountdownSnackbar
import com.example.common.ui.snackbar_controller.SnackbarController
import com.example.database.models.Source
import com.example.feature.decks.presentation.deck_details.DeckDetailScreen
import com.example.feature.decks.presentation.deck_details_statistic.DeckDetailsStatisticScreen
import com.example.feature.decks.presentation.public_decks.PublicDecksScreen
import com.example.feature.history.history.HistoryScreen
import com.example.feature.localdecks.presentation.add_edit_card.AddEditCardScreen
import com.example.feature.localdecks.presentation.add_edit_deck.AddEditDeckScreen
import com.example.feature.localdecks.presentation.local_decks.LocalDecksScreen
import com.example.feature.profile.presentation.owner_profile.OwnerProfileScreen
import com.example.feature.profile.presentation.profile.ProfileScreen
import com.example.feature.profile.presentation.settings.SettingsScreen
import com.example.feature.reminder.presentation.add_reminder.AddReminderScreen
import com.example.feature.reminder.presentation.reminder.ReminderScreen
import com.example.feature.training.presentation.training.TrainingScreen
import com.example.feature.training.presentation.training_errors.TrainingErrorsScreen
import com.example.feature.training.presentation.training_mode_settings.TrainingModeSettingsScreen
import com.example.feature.training.presentation.training_results.TrainingResultsScreen
import com.example.tprep.app.navigation.AppNavGraph
import com.example.tprep.app.navigation.Screen
import com.example.tprep.app.navigation.navigateToRoute
import com.example.tprep.app.navigation.rememberNavigationState
import com.example.tprep.app.presentation.utils.ObserveAsEvents
import com.example.tprep.app.presentation.utils.currentRoute
import com.example.tprep.app.presentation.utils.shouldShowBottomNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavHostController) {
    val navigationState = rememberNavigationState(navHostController = navController)
    val currentRoute = currentRoute(navigationState)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    event.action?.action?.invoke()
                }

                SnackbarResult.Dismissed -> {
                    event.action?.dismiss?.invoke()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { data ->
                CountdownSnackbar(data)
            }
        },
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
                PublicDecksScreen(
                    paddingValues = paddingValues,
                    onDeckClickListener = { deckId ->
                        navigationState.navigateTo(Screen.DeckDetails(deckId, Source.NETWORK))
                    },
                    onTrainClick = { deckId, source ->
                        navigationState.navigateWithSaveState(
                            Screen.Training(
                                deckId = deckId,
                                source = source
                            )
                        )
                    },
                    onScheduleClick = { deckId, deckName, source ->
                        navigationState.navigateWithSaveState(
                            Screen.Reminder(
                                deckId = deckId,
                                deckName = deckName,
                                source = source
                            )
                        )
                    }
                )
            },
            historyScreenContent = {
                HistoryScreen(
                    paddingValues = paddingValues,
                    onHistoryClick = { trainingSessionId ->
                        navigationState.navigateWithSaveState(
                            Screen.TrainingResults(
                                trainingSessionId = trainingSessionId,
                                cameFromHistoryScreen = true
                            )
                        )
                    }
                )
            },
            settingsScreenContent = {
                SettingsScreen(onBackClick = { navigationState.navHostController.popBackStack() })
            },
            profileScreenContent = {
                ProfileScreen(
                    paddingValues = paddingValues,
                    onLogoutClick = {
                        navigationState.navigateLogout(Screen.Auth)
                    },
                    onSettingsClick = {
                        navigationState.navigateWithSaveState(Screen.Settings)
                    }
                )
            },
            ownerProfileScreenContent = { ownerId ->
                OwnerProfileScreen(
                    ownerId = ownerId,
                    onBackClick = { navigationState.navHostController.popBackStack() },
                    onDeckClickListener = { deckId ->
                        navigationState.navigateWithSaveState(
                            Screen.DeckDetails(
                                deckId,
                                Source.NETWORK
                            )
                        )
                    },
                    onTrainClick = { deckId ->
                        navigationState.navigateWithSaveState(
                            Screen.Training(
                                deckId = deckId,
                                source = Source.NETWORK
                            )
                        )
                    },
                    onScheduleClick = { deckId, deckName ->
                        navigationState.navigateWithSaveState(
                            Screen.Reminder(
                                deckId = deckId,
                                deckName = deckName,
                                source = Source.NETWORK
                            )
                        )
                    }
                )
            },
            deckDetailsScreenContent = { deckId, source ->
                DeckDetailScreen(
                    deckId = deckId,
                    source = source,
                    onBackClick = { navigationState.navHostController.popBackStack() },
                    onStartTraining = { deckId, source ->
                        navigationState.navigateWithSaveState(
                            Screen.Training(
                                deckId = deckId,
                                source = source
                            )
                        )
                    },
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
                                deckId = it,
                                cardId = null
                            )
                        )
                    },
                    onRemindClick = { deckName ->
                        navigationState.navigateWithSaveState(
                            Screen.Reminder(
                                deckId = deckId,
                                source = source,
                                deckName = deckName
                            )
                        )
                    },
                    onTrainingModeSettingsClick = { deckId ->
                        navigationState.navigateWithSaveState(
                            Screen.TrainingModeSettings(deckId = deckId)
                        )
                    },
                    onDeckStatisticClick = { deckId ->
                        navigationState.navigateWithSaveState(
                            Screen.DeckDetailsStatistic(deckId = deckId)
                        )
                    },
                    onOwnerProfileClick = { ownerId ->
                        navigationState.navigateWithSaveState(
                            Screen.OwnerProfile(ownerId = ownerId)
                        )
                    }
                )
            },
            deckDetailsStatisticScreenContent = { deckId ->
                DeckDetailsStatisticScreen(
                    deckId = deckId,
                    onBackClick = { navigationState.navHostController.popBackStack() }
                )
            },
            trainingScreenContent = { deckId, source ->
                TrainingScreen(
                    deckId = deckId,
                    source = source,
                    onTrainingResultsClick = { trainingSessionId ->
                        navigationState.navigateToRemovePreviousScreen(
                            Screen.TrainingResults(
                                trainingSessionId,
                                false
                            )
                        )
                    },
                    onBackClick = { navigationState.navHostController.popBackStack() }
                )
            },
            trainingResultsScreenContent = { trainingSessionId, cameFromHistoryScreen ->
                TrainingResultsScreen(
                    trainingSessionId = trainingSessionId,
                    cameFromHistoryScreen = cameFromHistoryScreen,
                    onBackClick = { navigationState.navHostController.popBackStack() },
                    onNavigateToDeck = { deckId, source ->
                        navigationState.navigateToRemovePreviousScreen(
                            Screen.DeckDetails(
                                deckId = deckId,
                                source = source
                            )
                        )
                    },
                    onErrorsClick = { trainingSessionId ->
                        navigationState.navigateWithSaveState(
                            Screen.TrainingErrors(
                                trainingSessionId
                            )
                        )
                    }
                )
            },
            trainingErrorsScreenContent = { trainingSessionId ->
                TrainingErrorsScreen(
                    trainingSessionId = trainingSessionId,
                    onBackClick = { navigationState.navHostController.popBackStack() }
                )
            },
            trainingModeSettingsScreenContent = { deckId ->
                TrainingModeSettingsScreen(
                    deckId = deckId,
                    onBackClick = { navigationState.navHostController.popBackStack() })
            },
            localDecksScreenContent = {
                LocalDecksScreen(
                    paddingValues = paddingValues,
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
                    },
                    onTrainClick = { deckId ->
                        navigationState.navigateWithSaveState(
                            Screen.Training(
                                deckId = deckId,
                                source = Source.LOCAL
                            )
                        )
                    },
                    onScheduleClick = { deckId, deckName ->
                        navigationState.navigateWithSaveState(
                            Screen.Reminder(
                                deckId = deckId,
                                deckName = deckName,
                                source = Source.LOCAL
                            )
                        )
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