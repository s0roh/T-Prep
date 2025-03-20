package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "error_answer",
    indices = [
        Index(value = ["cardId"]),
        Index(value = ["trainingSessionId"])
    ]
)
data class ErrorAnswerDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("trainingSessionId") val trainingSessionId: String,
    @ColumnInfo("cardId") val cardId: Int,
    @ColumnInfo("question") val question: String,
    @ColumnInfo("answer") val answer: String,
    @ColumnInfo("userAnswer") val userAnswer: String,
    @ColumnInfo("blankAnswer") val blankAnswer: String?,
    @ColumnInfo("trainingMode") val trainingMode: TrainingMode,
)

enum class TrainingMode {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    FILL_IN_THE_BLANK
}