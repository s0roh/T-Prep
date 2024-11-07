package com.example.t_prep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.example.t_prep.presentation.publicDecks.PublicDecksScreen
import com.example.t_prep.presentation.ui.theme.TPrepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPrepTheme {
                Scaffold {innerPadding ->
                    PublicDecksScreen(innerPadding)
                    //LoginScreen()
                }
            }
        }
    }
}