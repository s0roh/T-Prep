package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "decks",
    indices = [
        Index(value = ["isHide"]),
        Index(value = ["isDeleted"])
    ]
)
data class DeckDBO(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo("serverDeckId") val serverDeckId: String?,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("isPublic") val isPublic: Boolean,
    @ColumnInfo("isHide") val isHide: Boolean = false,
    @ColumnInfo("isDeleted") val isDeleted: Boolean = false,
    @ColumnInfo("userId") val userId: String
)