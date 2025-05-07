package com.example.localdecks.data.mapper

import com.example.network.dto.global.MetricsDto
import com.example.preferences.metrics.entity.Metrics

internal fun Metrics.toDto(): MetricsDto = MetricsDto(
    favouriteFilterButton = favouriteFilterButton,
    favouriteProfileButton = favouriteProfileButton,
    lastInAppTime = lastInAppTime,
    sumTrainingsTime = sumTrainingsTime,
    trainingsCount = trainingsCount
)