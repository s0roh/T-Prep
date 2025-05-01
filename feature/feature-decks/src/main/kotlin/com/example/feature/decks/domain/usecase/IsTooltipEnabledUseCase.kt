package com.example.feature.decks.domain.usecase

import com.example.database.models.Source
import com.example.preferences.TooltipPreferences
import javax.inject.Inject

internal class IsTooltipEnabledUseCase @Inject constructor(
    private val preferences: TooltipPreferences,
) {

    operator fun invoke(source: Source): Boolean = when (source) {
        Source.LOCAL -> preferences.isLocalTooltipEnabled()
        Source.NETWORK -> preferences.isPublicTooltipEnabled()
    }
}