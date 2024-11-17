package com.example.t_prep.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.feature_auth.presentation.LoginScreen
import com.example.feature_decks.presentation.PublicDecksScreen
import com.example.t_prep.navigation.AppNavGraph
import com.example.t_prep.navigation.Screen
import com.example.t_prep.navigation.rememberNavigationState
import com.example.t_prep.presentation.components.AppBottomNavigation
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
                        navigationState.navigateTo(Screen.DeckDetails(it))
                    }
                )
            },
            profileScreenContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CenteredTextScreen("Profile")
                }
            },
            deckDetailsScreenContent = { deckId ->
                CenteredTextScreen("DeckDetails with id: $deckId")
            }
        )
    }
}

@Composable
private fun CenteredTextScreen(name: String) {
    var count by rememberSaveable { mutableIntStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.clickable { count++ },
            text = "$name Count: $count",
            fontSize = MaterialTheme.typography.headlineMedium.fontSize
        )
    }
}