package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "correct_answer",
    indices = [
        Index(value = ["cardId"]),
        Index(value = ["trainingSessionId"])
    ]
)
data class CorrectAnswerDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("cardId") val cardId: Int,
    @ColumnInfo("trainingMode") val trainingMode: TrainingMode,
    @ColumnInfo("trainingSessionId") val trainingSessionId: String,
)
