package com.example.feature.decks.domain.usecase

import com.example.database.models.Source
import com.example.preferences.tooltip.TooltipPreferences
import javax.inject.Inject

internal class SetTooltipShownUseCase @Inject constructor(
    private val preferences: TooltipPreferences,
) {

    operator fun invoke(source: Source) = when (source) {
        Source.LOCAL -> preferences.setLocalTooltipShown()
        Source.NETWORK -> preferences.setPublicTooltipShown()
    }
}