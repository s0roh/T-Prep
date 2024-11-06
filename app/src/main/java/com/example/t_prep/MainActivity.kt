package com.example.t_prep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.t_prep.presentation.login.LoginScreen
import com.example.t_prep.presentation.ui.theme.TPrepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPrepTheme {
                LoginScreen()
            }
        }
    }
}