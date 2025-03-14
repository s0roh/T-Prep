package com.example.feature.decks.presentation.util

import com.example.database.models.TrainingMode

internal fun TrainingMode.toLabel(): String {
    return when (this) {
        TrainingMode.MULTIPLE_CHOICE -> "Выбор"
        TrainingMode.TRUE_FALSE -> "Правда/Ложь"
        TrainingMode.FILL_IN_THE_BLANK -> "Дополнить ответ"
    }
}