package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.HistoryDBO
import com.example.database.models.Source

@Dao
interface HistoryDao {

    @Query(
        """
    SELECT * 
    FROM history
    WHERE timestamp = (
        SELECT MAX(h2.timestamp)
        FROM history h2
        WHERE h2.deckId = history.deckId AND h2.source = history.source AND h2.userId = history.userId
    )
    AND userId = :userId
    ORDER BY timestamp DESC
    """
    )
    suspend fun getLastTrainingPerDeck(userId: String): List<HistoryDBO>

    @Query(
        """
        SELECT * FROM history 
        WHERE cardId = :cardId AND deckId = :deckId AND source = :source AND userId = :userId
    """
    )
    suspend fun getHistoryForCard(cardId: Int, deckId: String, source: Source, userId: String): HistoryDBO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHistory(history: HistoryDBO)

    @Query("DELETE FROM history WHERE deckId = :deckId")
    suspend fun deleteHistoryForDeck(deckId: String)
}