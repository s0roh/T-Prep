package com.example.training.domain.entity

import com.example.database.models.TrainingMode

data class TrainingModes(
    val deckId: String,
    val modes: List<TrainingMode>
)
