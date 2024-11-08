package com.example.t_prep.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginScreenContent: @Composable () -> Unit,
    publicDecksScreenContent: @Composable (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ){
        composable<Screen.Login>{
            loginScreenContent()
        }
        composable<Screen.PublicDecks>{
            val args = it.toRoute<Screen.PublicDecks>()
            publicDecksScreenContent(args.token)
        }
    }
}