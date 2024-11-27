package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("serverId") val serverId: Long?,
    @ColumnInfo("deckId") val deckId: Long,
    @ColumnInfo("question") val question: String,
    @ColumnInfo("answer") val answer: String,
    @ColumnInfo("status") val status: CardStatus,
)

enum class CardStatus {
    NEW,       // Новая карточка, еще не синхронизирована
    UPDATED,   // Карточка изменена
    DELETED,   // Карточка удалена
    SYNCED     // Карточка синхронизирована
}
