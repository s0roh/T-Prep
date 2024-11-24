package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    indices = [
        Index(value = ["cardId"]),
        Index(value = ["deckId"]),
        Index(value = ["source"])
    ]
)
data class HistoryDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("deckId") val deckId: Long,
    @ColumnInfo("cardId") val cardId: Long,
    @ColumnInfo("timestamp") val timestamp: Long,
    @ColumnInfo("isCorrect") val isCorrect: Boolean,
    @ColumnInfo("source") val source: Source
)

enum class Source {
    LOCAL,
    NETWORK
}