package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Update
import com.example.database.models.AnswerStatsDBO
import com.example.database.models.HistoryDBO

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryDBO)

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("""
    SELECT 
        (SELECT COUNT(*) FROM correct_answer ca 
         JOIN history h ON ca.trainingSessionId = h.trainingSessionId
         WHERE ca.cardId = :cardId AND h.deckId = :deckId AND h.userId = :userId) AS correctCount,
         
        (SELECT COUNT(*) FROM error_answer ea 
         JOIN history h ON ea.trainingSessionId = h.trainingSessionId
         WHERE ea.cardId = :cardId AND h.deckId = :deckId AND h.userId = :userId) AS errorCount
""")
    suspend fun getAnswerStatsForCard(
        cardId: Int,
        deckId: String,
        userId: String,
    ): AnswerStatsDBO

    @Query("""
    SELECT 
        (SELECT COUNT(*) FROM correct_answer WHERE trainingSessionId = :trainingSessionId) AS correctCount,
        (SELECT COUNT(*) FROM error_answer WHERE trainingSessionId = :trainingSessionId) AS errorCount,
        (SELECT cardsCount FROM history WHERE trainingSessionId = :trainingSessionId) AS allCount
""")
    suspend fun getAnswerStatsForSession(trainingSessionId: String): AnswerStatsDBO

    @Query("SELECT * FROM history WHERE deckId = :deckId AND userId = :userId ORDER BY timestamp ASC")
    suspend fun getHistoryForDeck(deckId: String, userId: String): List<HistoryDBO>

    @Query("SELECT * FROM history WHERE trainingSessionId = :trainingSessionId")
    suspend fun getHistoryForTrainingSession(trainingSessionId: String): HistoryDBO?

    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllTrainingHistories(userId: String): List<HistoryDBO>

    @Query("SELECT * FROM history WHERE userId = :userId AND isSynchronized = 0")
    suspend fun getHistoryToSync(userId: String): List<HistoryDBO>

    @Query("SELECT MAX(timestamp) FROM history WHERE userId = :userId AND isSynchronized = 1")
    suspend fun getLastSyncTime(userId: String): Long?

    @Query("DELETE FROM history WHERE deckId = :deckId")
    suspend fun deleteHistoryForDeck(deckId: String)

    @Query("SELECT * FROM history WHERE trainingSessionId = :trainingSessionId LIMIT 1")
    suspend fun getHistoryByTrainingSessionId(trainingSessionId: String): HistoryDBO?

    @Update
    suspend fun updateHistory(history: HistoryDBO)
}