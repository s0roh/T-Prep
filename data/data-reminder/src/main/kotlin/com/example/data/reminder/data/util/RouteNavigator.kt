package com.example.data.reminder.data.util

import android.content.Intent
import com.example.database.models.Source

interface RouteNavigator {

    fun getDeckDetailsRoute(deckId: String, source: Source): String

    fun createDeckIntent(route: String): Intent
}