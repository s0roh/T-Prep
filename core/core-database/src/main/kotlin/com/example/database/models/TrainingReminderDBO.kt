package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_reminders",
)
data class TrainingReminderDBO(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reminderTime: Long,
    val name: String,
    val source: Source,
    val deckId: Long
)