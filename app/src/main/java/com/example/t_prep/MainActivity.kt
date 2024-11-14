package com.example.t_prep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.example.feature_auth.presentation.LoginScreen
import com.example.t_prep.presentation.navigation.AppNavGraph
import com.example.t_prep.presentation.navigation.Screen
import com.example.feature_decks.presentation.PublicDecksScreen
import com.example.t_prep.presentation.ui.theme.TPrepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPrepTheme {
                Scaffold { paddingValues ->
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        loginScreenContent = {
                            LoginScreen(
                                onSuccessLoginListener = {
                                    navController.navigate(Screen.PublicDecks(it)) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        },
                        publicDecksScreenContent = { token ->
                            PublicDecksScreen(
                                paddingValues = paddingValues,
                                token = token,
                                onDeckClickListener = {
                                    TODO()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}