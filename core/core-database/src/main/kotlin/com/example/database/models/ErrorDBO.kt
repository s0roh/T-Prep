package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "errors",
    indices = [
        Index(value = ["trainingSessionId"])
    ]
)
data class ErrorDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("trainingSessionId") val trainingSessionId: String,
    @ColumnInfo("deckId") val deckId: String,
    @ColumnInfo("cardId") val cardId: Int,
    @ColumnInfo("incorrectAnswer") val incorrectAnswer: String,
    @ColumnInfo("trainingMode") val trainingMode: TrainingMode,
)

enum class TrainingMode {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    FILL_IN_THE_BLANK
}