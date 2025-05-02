package com.example.preferences.tooltip

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TooltipPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context,
) : TooltipPreferences {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isLocalTooltipEnabled(): Boolean = prefs.getBoolean(LOCAL_HINT_ENABLED_KEY, true)

    override fun isPublicTooltipEnabled(): Boolean = prefs.getBoolean(PUBLIC_HINT_ENABLED_KEY, true)

    override fun isPublicDecksTooltipEnabled(): Boolean = prefs.getBoolean(
        PUBLIC_DECKS_HINT_ENABLED_KEY, true
    )

    override fun setLocalTooltipShown() {
        prefs.edit { putBoolean(LOCAL_HINT_ENABLED_KEY, false) }
    }

    override fun setPublicTooltipShown() {
        prefs.edit { putBoolean(PUBLIC_HINT_ENABLED_KEY, false) }
    }

    override fun setPublicDecksTooltipShown() {
        prefs.edit { putBoolean(PUBLIC_DECKS_HINT_ENABLED_KEY, false) }
    }

    companion object {

        private const val PREFS_NAME = "tooltip_prefs"
        private const val LOCAL_HINT_ENABLED_KEY = "local_hint_enabled"
        private const val PUBLIC_HINT_ENABLED_KEY = "public_hint_enabled"
        private const val PUBLIC_DECKS_HINT_ENABLED_KEY = "public_decks_hint_enabled"
    }
}