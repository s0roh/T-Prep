package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    indices = [
        Index(value = ["deckId"]),
        Index(value = ["timestamp"]),
        Index(value = ["cardId", "deckId", "source"]),
        Index(value = ["trainingSessionId"])
    ]
)
data class HistoryDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("deckId") val deckId: String,
    @ColumnInfo("deckName") val deckName: String,
    @ColumnInfo("cardsCount") val cardsCount: Int,
    @ColumnInfo("cardId") val cardId: Int,
    @ColumnInfo("timestamp") val timestamp: Long,
    @ColumnInfo("isCorrect") val isCorrect: Boolean,
    @ColumnInfo("source") val source: Source,
    @ColumnInfo("trainingSessionId") val trainingSessionId: String,
    @ColumnInfo("userId") val userId: String,
)

enum class Source {
    LOCAL,
    NETWORK
}