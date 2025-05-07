package com.example.feature.training.domain

import com.example.preferences.metrics.MetricsPreferences
import javax.inject.Inject

internal class IncrementTrainingsCountMetricUseCase @Inject constructor(
    private val preferences: MetricsPreferences
) {

    operator fun invoke() = preferences.incrementTrainingsCount()
}