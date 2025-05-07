package com.example.feature.profile.domain

import com.example.preferences.metrics.MetricsPreferences
import javax.inject.Inject

internal class IncrementFavouriteProfileButtonMetricUseCase @Inject constructor(
    private val preferences: MetricsPreferences
){

    operator fun invoke() = preferences.incrementFavouriteProfileButton()
}