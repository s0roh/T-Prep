package com.example.feature.decks.domain.usecase

import com.example.preferences.TooltipPreferences
import javax.inject.Inject

internal class IsPublicDecksTooltipEnabledUseCase @Inject constructor(
    private val preferences: TooltipPreferences
) {

    operator fun invoke(): Boolean = preferences.isPublicDecksTooltipEnabled()
}