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
        WHERE h2.deckId = history.deckId
    )
    ORDER BY timestamp DESC
    """
    )
    suspend fun getLastTrainingPerDeck(): List<HistoryDBO>

    @Query(
        """
        SELECT * FROM history 
        WHERE cardId = :cardId AND deckId = :deckId AND source = :source
    """
    )
    suspend fun getHistoryForCard(cardId: Long, deckId: Long, source: Source): HistoryDBO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHistory(history: HistoryDBO)

}