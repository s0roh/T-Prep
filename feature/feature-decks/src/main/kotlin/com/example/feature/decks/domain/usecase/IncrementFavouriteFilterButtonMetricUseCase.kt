package com.example.feature.decks.domain.usecase

import com.example.preferences.metrics.MetricsPreferences
import javax.inject.Inject

internal class IncrementFavouriteFilterButtonMetricUseCase @Inject constructor(
    private val preferences: MetricsPreferences
) {

    operator fun invoke() = preferences.incrementFavouriteFilterButton()
}