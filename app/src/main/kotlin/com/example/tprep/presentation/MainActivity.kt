package com.example.tprep.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.auth.presentation.login.LoginScreen
import com.example.database.models.Source
import com.example.decks.presentation.details.DeckDetailScreen
import com.example.decks.presentation.publicdecks.PublicDecksScreen
import com.example.history.presentation.history.HistoryScreen
import com.example.localdecks.presentation.add_edit_card.AddEditCardScreen
import com.example.localdecks.presentation.add_edit_deck.AddEditDeckScreen
import com.example.localdecks.presentation.local_decks.LocalDecksScreen
import com.example.tprep.navigation.AppNavGraph
import com.example.tprep.navigation.Screen
import com.example.tprep.navigation.rememberNavigationState
import com.example.tprep.presentation.components.AppBottomNavigation
import com.example.tprep.presentation.components.CenteredPlaceholderTextScreen
import com.example.tprep.presentation.ui.theme.TPrepTheme
import com.example.tprep.presentation.utils.currentRoute
import com.example.tprep.presentation.utils.shouldShowBottomNavigation
import com.example.training.presentation.training.TrainingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPrepTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navigationState = rememberNavigationState()
    val currentRoute = currentRoute(navigationState)

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNavigation(currentRoute)) {
                AppBottomNavigation(navigationState = navigationState)
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navHostController = navigationState.navHostController,
            loginScreenContent = {
                LoginScreen(
                    onSuccessLoginListener = {
                        navigationState.navigateFromLogin(Screen.PublicDecks)
                    }
                )
            },
            publicDecksScreenContent = {
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CenteredPlaceholderTextScreen("Profile")
                }
            },
            deckDetailsScreenContent = { deckId, source ->
                // TODO Заменить временное значение deckId = 2 на реальный идентификатор,
                // получаемый из параметра deckId. Сейчас используется константа для тестирования.
                var temporaryDeckId: Long = deckId
                //if (deckId > 2) temporaryDeckId = 2

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
                    onDeleteDeck = {
                        navigationState.navHostController.popBackStack()
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
                                deckId = deckId,
                                cardId = null
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
                    paddingValues = paddingValues,
                    onDeckClick = { deckId ->
                        navigationState.navigateWithLocalDecksRefresh(
                            Screen.DeckDetails(
                                deckId,
                                Source.LOCAL
                            )
                        )
                    },
                    onAddClick = {
                        navigationState.navigateWithSaveState(Screen.AddEditDeck(deckId = null))
                    })
            },
            addEditDeckScreenContent = { deckId ->
                AddEditDeckScreen(
                    deckId = deckId,
                    paddingValues = paddingValues,
                    onBackClick = { navigationState.navHostController.popBackStack() },
                    onSaveClick = { navigationState.navHostController.popBackStack() }
                )
            },
            addEditCardScreenContent = { deckId, cardId ->
                AddEditCardScreen(
                    cardId = cardId,
                    deckId = deckId,
                    paddingValues = paddingValues,
                    onBackClick = { navigationState.navHostController.popBackStack() },
                    onSaveClick = { navigationState.navHostController.popBackStack() }
                )
            }
        )
    }
}