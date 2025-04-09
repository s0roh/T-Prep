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
    @ColumnInfo("wrongAnswer1") val wrongAnswer1: String? = null,
    @ColumnInfo("wrongAnswer2") val wrongAnswer2: String? = null,
    @ColumnInfo("wrongAnswer3") val wrongAnswer3: String? = null,
    @ColumnInfo("attachment") val attachment: String? = null,
    @ColumnInfo("isDeleted") val isDeleted: Boolean = false,
)