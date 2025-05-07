package com.example.tprep.app.utils.metrics

import com.example.preferences.metrics.MetricsPreferences
import javax.inject.Inject

class AppSessionTracker @Inject constructor(
    private val preferences: MetricsPreferences,
) {
    private var sessionStart: Long = 0

    fun onStart() {
        sessionStart = System.currentTimeMillis()
    }

    fun onStop() {
        val duration = System.currentTimeMillis() - sessionStart
        if (duration > 0) {
            preferences.addLastInAppTime((duration / 1000).toInt())
        }
    }
}