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
    ]
)
data class HistoryDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("deckId") val deckId: Long,
    @ColumnInfo("deckName") val deckName: String,
    @ColumnInfo("cardsCount") val cardsCount: Int,
    @ColumnInfo("cardId") val cardId: Long,
    @ColumnInfo("timestamp") val timestamp: Long,
    @ColumnInfo("isCorrect") val isCorrect: Boolean,
    @ColumnInfo("source") val source: Source,
    @ColumnInfo("coefficient") val coefficient: Double
)

enum class Source {
    LOCAL,
    NETWORK
}