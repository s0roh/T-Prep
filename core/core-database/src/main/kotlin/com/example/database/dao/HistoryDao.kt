package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.HistoryDBO
import com.example.database.models.Source

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryDBO)

    @Query("SELECT * FROM history WHERE cardId = :cardId AND deckId = :deckId AND source = :source AND userId = :userId")
    suspend fun getHistoryForCard(
        cardId: Int,
        deckId: String,
        source: Source,
        userId: String,
    ): List<HistoryDBO>

    @Query("SELECT * FROM history WHERE deckId = :deckId AND userId = :userId ORDER BY timestamp ASC")
    suspend fun getHistoryForDeck(deckId: String, userId: String): List<HistoryDBO>

    @Query("SELECT * FROM history WHERE trainingSessionId = :trainingSessionId")
    suspend fun getHistoryForTrainingSession(trainingSessionId: String): List<HistoryDBO>

    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllTrainingHistories(userId: String): List<HistoryDBO>

    @Query("DELETE FROM history WHERE deckId = :deckId")
    suspend fun deleteHistoryForDeck(deckId: String)
}