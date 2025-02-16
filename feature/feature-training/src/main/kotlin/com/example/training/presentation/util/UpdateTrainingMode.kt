package com.example.training.presentation.util

import com.example.database.models.TrainingMode
import com.example.training.domain.entity.TrainingModes

internal fun updateMode(
    modes: TrainingModes,
    mode: TrainingMode,
    isChecked: Boolean,
): TrainingModes {
    val currentModes = modes.modes.toMutableSet()

    if (isChecked) {
        currentModes.add(mode)
    } else if (currentModes.size > 1) {
        currentModes.remove(mode)
    }

    return modes.copy(modes = currentModes.toList())
}