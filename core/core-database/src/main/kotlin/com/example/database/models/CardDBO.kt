package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardDBO(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo("serverCardId") val serverCardId: Int?,
    @ColumnInfo("deckId") val deckId: String,
    @ColumnInfo("question") val question: String,
    @ColumnInfo("answer") val answer: String,
    @ColumnInfo("isDeleted") val isDeleted: Boolean = false,
)