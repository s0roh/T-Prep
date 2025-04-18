package com.example.feature.profile.domain

import com.example.data.profile.domain.repository.SettingsRepository
import javax.inject.Inject

internal class ToggleSoundUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {

    operator fun invoke() = repository.toggleSound()
}