package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckDBO(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("serverDeckId") val serverDeckId: Long?,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("isPublic") val isPublic: Boolean
)