package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.database.models.HistoryDBO
import com.example.database.models.Source

@Dao
interface HistoryDao {

    @Query("""
         SELECT * 
    FROM history h
    WHERE h.timestamp = (
    -- Находим максимальное время тренировки для текущей колоды
        SELECT MAX(h2.timestamp) 
        FROM history h2 
        WHERE h2.deckId = h.deckId
    )
    -- Сортируем результат по времени тренировки
    ORDER BY h.timestamp DESC
    """)
    suspend fun getLastTrainingPerDeck(): List<HistoryDBO>

    @Insert
    suspend fun insertHistory(history: HistoryDBO)

    @Query("SELECT COUNT(*) FROM history WHERE cardId = :cardId AND deckId = :deckId AND isCorrect = 1 AND source = :source")
    suspend fun getCorrectAnswersCountForCard(cardId: Long, deckId: Long, source: Source): Int

    @Query("SELECT COUNT(*) FROM history WHERE cardId = :cardId AND deckId = :deckId AND source = :source")
    suspend fun getTotalAnswersCount(cardId: Long, deckId: Long, source: Source): Int
}