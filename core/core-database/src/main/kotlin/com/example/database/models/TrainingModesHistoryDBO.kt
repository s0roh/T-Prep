package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("training_modes_history")
data class TrainingModesHistoryDBO(
    @PrimaryKey val deckId: String,
    @ColumnInfo("multipleChoiceEnabled") val multipleChoiceEnabled: Boolean,
    @ColumnInfo("trueFalseEnabled") val trueFalseEnabled: Boolean,
    @ColumnInfo("fillInTheBlankEnabled") val fillInTheBlankEnabled: Boolean
)