package com.example.feature.training.domain

import com.example.data.profile.domain.repository.SettingsRepository
import javax.inject.Inject

internal class IsVibrationEnabledUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {

    operator fun invoke(): Boolean = repository.isVibrationEnabled()
}