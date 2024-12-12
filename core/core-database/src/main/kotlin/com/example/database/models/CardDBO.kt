package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardDBO(
    @PrimaryKey val id: Long,
    @ColumnInfo("serverCardId") val serverCardId: Long?,
    @ColumnInfo("deckId") val deckId: Long,
    @ColumnInfo("question") val question: String,
    @ColumnInfo("answer") val answer: String
)