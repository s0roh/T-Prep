package com.example.t_prep.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.feature_auth.presentation.login.LoginScreen
import com.example.feature_decks.presentation.details.DeckDetailScreen
import com.example.feature_decks.presentation.public_decks.PublicDecksScreen
import com.example.t_prep.navigation.AppNavGraph
import com.example.t_prep.navigation.Screen
import com.example.t_prep.navigation.rememberNavigationState
import com.example.t_prep.presentation.components.AppBottomNavigation
import com.example.t_prep.presentation.components.CenteredPlaceholderTextScreen
import com.example.t_prep.presentation.ui.theme.TPrepTheme
import com.example.t_prep.presentation.utils.currentRoute
import com.example.t_prep.presentation.utils.shouldShowBottomNavigation
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
            AnimatedVisibility(
                visible = shouldShowBottomNavigation(currentRoute),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
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
                        navigationState.navigateTo(Screen.DeckDetails(it))
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
            deckDetailsScreenContent = { deckId ->
                // TODO Заменить временное значение deckId = 2 на реальный идентификатор,
                // получаемый из параметра deckId. Сейчас используется константа для тестирования.
                DeckDetailScreen(
                    deckId = 2,
                    paddingValues = paddingValues,
                )
            }
        )
    }
}