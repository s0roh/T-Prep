package com.example.tprep.app.reminder

import android.content.Context
import android.content.Intent
import com.example.database.models.Source
import com.example.data.reminder.data.util.RouteNavigator
import com.example.tprep.app.navigation.Screen
import com.example.tprep.app.presentation.MainActivity
import javax.inject.Inject

class AppRouteNavigator @Inject constructor(private val context: Context) : RouteNavigator {

    override fun getDeckDetailsRoute(deckId: String, source: Source): String {
        return Screen.DeckDetails(deckId, source).toRoute()
    }

    override fun createDeckIntent(route: String): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", route)
        }
    }
}