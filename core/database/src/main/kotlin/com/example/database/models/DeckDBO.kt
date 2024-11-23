package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("serverId") val serverId: Long?,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("isPublic") val isPublic: Boolean,
    @ColumnInfo("status") val status: DeckStatus,
    @ColumnInfo("lastUpdated") val lastUpdated: Long
)

enum class DeckStatus {
    NEW,       // Новая колода, еще не синхронизирована с сервером
    UPDATED,   // Колода изменена локально
    DELETED,   // Колода удалена локально
    SYNCED     // Колода синхронизирована с сервером
}