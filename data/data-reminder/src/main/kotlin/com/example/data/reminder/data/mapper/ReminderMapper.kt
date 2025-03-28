package com.example.data.reminder.data.mapper

import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.TrainingReminderDBO

internal fun Reminder.toDBO(): TrainingReminderDBO = TrainingReminderDBO(
    id = id,
    reminderTime = reminderTime,
    name = name,
    source = source,
    deckId = deckId
)

internal fun TrainingReminderDBO.toEntity() = Reminder(
    id = id,
    reminderTime = reminderTime,
    name = name,
    source = source,
    deckId = deckId
)