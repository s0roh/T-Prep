package com.example.data.reminder.domain.entity

import com.example.database.models.Source

data class Reminder(
    val id: Long = 0,
    val reminderTime: Long,
    val name: String,
    val source: Source,
    val deckId: Long
)
