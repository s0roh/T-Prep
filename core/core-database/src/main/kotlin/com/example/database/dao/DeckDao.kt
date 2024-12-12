package com.example.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.database.models.DeckDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Query("SELECT * FROM decks ORDER BY id DESC")
    fun getDecks(): Flow<List<DeckDBO>>

    @Query("SELECT * FROM decks WHERE id = :deckId")
    suspend fun getDeckById(deckId: String): DeckDBO?

    @Query("SELECT id FROM decks WHERE id = :id")
    suspend fun getDeckId(id: String): String?

    suspend fun insertDeck(deckDBO: DeckDBO): String {
        privateInsertDeck(deckDBO)
        return getDeckId(deckDBO.id) ?: throw IllegalStateException("Insert failed")
    }

    @Update
    suspend fun updateDeck(deckDBO: DeckDBO)

    @Delete
    suspend fun deleteDeck(deckDBO: DeckDBO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun privateInsertDeck(deckDBO: DeckDBO)
}