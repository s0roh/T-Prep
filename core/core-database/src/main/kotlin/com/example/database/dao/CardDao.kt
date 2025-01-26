package com.example.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.database.models.CardDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND isDeleted = 0")
    fun getCardsForDeck(deckId: String): Flow<List<CardDBO>>

    @Query("SELECT * FROM cards WHERE id = :cardID")
    suspend fun getCardById(cardID: Int): CardDBO?

    @Query("SELECT * FROM cards WHERE serverCardId = :serverCardId AND deckId = :deckId")
    fun getCardByServerId(serverCardId: Int, deckId: String): CardDBO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cardDBO: CardDBO): Long

    @Update
    suspend fun updateCard(cardDBO: CardDBO)

    @Delete
    suspend fun deleteCard(cardDBO: CardDBO)
}