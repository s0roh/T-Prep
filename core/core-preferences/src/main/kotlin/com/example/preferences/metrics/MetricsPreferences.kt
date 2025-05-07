package com.example.preferences.metrics

import com.example.preferences.metrics.entity.Metrics

interface MetricsPreferences {

    fun incrementFavouriteFilterButton()
    fun incrementFavouriteProfileButton()
    fun addLastInAppTime(seconds: Int)
    fun addTrainingsTime(seconds: Int)
    fun incrementTrainingsCount()

    fun getMetrics(): Metrics
    fun resetMetrics()
}