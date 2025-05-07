package com.example.preferences.metrics

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.preferences.metrics.entity.Metrics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MetricsPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context,
) : MetricsPreferences {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun incrementFavouriteFilterButton() {
        increment(METRIC_FAVOURITE_FILTER)
    }

    override fun incrementFavouriteProfileButton() {
        increment(METRIC_FAVOURITE_PROFILE)
    }

    override fun addLastInAppTime(seconds: Int) {
        add(METRIC_LAST_IN_APP_TIME, seconds)
    }

    override fun addTrainingsTime(seconds: Int) {
        add(METRIC_SUM_TRAININGS_TIME, seconds)
    }

    override fun incrementTrainingsCount() {
        increment(METRIC_TRAININGS_COUNT)
    }

    override fun getMetrics(): Metrics {
        return Metrics(
            favouriteFilterButton = prefs.getInt(METRIC_FAVOURITE_FILTER, 0),
            favouriteProfileButton = prefs.getInt(METRIC_FAVOURITE_PROFILE, 0),
            lastInAppTime = prefs.getInt(METRIC_LAST_IN_APP_TIME, 0),
            sumTrainingsTime = prefs.getInt(METRIC_SUM_TRAININGS_TIME, 0),
            trainingsCount = prefs.getInt(METRIC_TRAININGS_COUNT, 0)
        )
    }

    override fun resetMetrics() {
        prefs.edit {
            putInt(METRIC_FAVOURITE_FILTER, 0)
            putInt(METRIC_FAVOURITE_PROFILE, 0)
            putInt(METRIC_LAST_IN_APP_TIME, 0)
            putInt(METRIC_SUM_TRAININGS_TIME, 0)
            putInt(METRIC_TRAININGS_COUNT, 0)
        }
    }

    private fun increment(key: String) {
        val value = prefs.getInt(key, 0)
        prefs.edit { putInt(key, value + 1) }
    }

    private fun add(key: String, delta: Int) {
        val value = prefs.getInt(key, 0)
        prefs.edit { putInt(key, value + delta) }
    }

    companion object {
        private const val PREFS_NAME = "metrics_prefs"

        private const val METRIC_FAVOURITE_FILTER = "favourite_filter_button"
        private const val METRIC_FAVOURITE_PROFILE = "favourite_profile_button"
        private const val METRIC_LAST_IN_APP_TIME = "last_in_app_time"
        private const val METRIC_SUM_TRAININGS_TIME = "sum_trainings_time"
        private const val METRIC_TRAININGS_COUNT = "trainings_count"
    }
}