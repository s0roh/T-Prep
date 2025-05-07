package com.example.tprep.app.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.localdecks.util.startSyncWork
import com.example.tprep.app.navigation.navigateToRoute
import com.example.tprep.app.presentation.components.MainScreen
import com.example.tprep.app.presentation.ui.theme.TPrepTheme
import com.example.tprep.app.utils.metrics.AppSessionTracker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    @Inject
    lateinit var sessionTracker: AppSessionTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        startSyncWork(context = this, shouldSyncMetrics = true)
        enableEdgeToEdge()
        setContent {
            TPrepTheme {
                navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        sessionTracker.onStart()
    }

    override fun onStop() {
        super.onStop()
        sessionTracker.onStop()
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