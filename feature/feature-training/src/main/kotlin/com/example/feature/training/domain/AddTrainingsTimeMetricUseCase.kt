package com.example.feature.training.domain

import com.example.preferences.metrics.MetricsPreferences
import javax.inject.Inject

internal class AddTrainingsTimeMetricUseCase @Inject constructor(
    private val preferences: MetricsPreferences,
) {

    operator fun invoke(seconds: Int) = preferences.addTrainingsTime(seconds = seconds)
}