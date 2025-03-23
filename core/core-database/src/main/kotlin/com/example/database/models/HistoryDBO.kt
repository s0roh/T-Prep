package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["deckId"]),
        Index(value = ["trainingSessionId"])
    ]
)
data class HistoryDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("userId") val userId: String,
    @ColumnInfo("deckId") val deckId: String,
    @ColumnInfo("deckName") val deckName: String,
    @ColumnInfo("cardsCount") val cardsCount: Int,
    @ColumnInfo("timestamp") val timestamp: Long,
    @ColumnInfo("trainingSessionId") val trainingSessionId: String,
    @ColumnInfo("source") val source: Source,
    @ColumnInfo("isSynchronized") val isSynchronized: Boolean,
)

enum class Source {
    LOCAL,
    NETWORK
}