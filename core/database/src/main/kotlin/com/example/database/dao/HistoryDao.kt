package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.database.models.HistoryDBO
import com.example.database.models.Source

@Dao
interface HistoryDao {

    // Получить всю историю, отсортированную по времени (от нового к старому)
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    suspend fun getAllHistory(): List<HistoryDBO>

    // Добавить запись в историю
    @Insert
    suspend fun insertHistory(history: HistoryDBO)

    @Query("SELECT COUNT(*) FROM history WHERE cardId = :cardId AND deckId = :deckId AND source = :source")
    suspend fun isCardNew(cardId: Long, deckId: Long, source: Source): Boolean

    // Получить количество правильных ответов для конкретной карточки в конкретной колоде, учитывая источник
    @Query("SELECT COUNT(*) FROM history WHERE cardId = :cardId AND deckId = :deckId AND isCorrect = 1 AND source = :source")
    suspend fun getCorrectAnswersCountForCard(cardId: Long, deckId: Long, source: Source): Int

    // Получить общее количество ответов для карточки в конкретной колоде, учитывая источник
    @Query("SELECT COUNT(*) FROM history WHERE cardId = :cardId AND deckId = :deckId AND source = :source")
    suspend fun getTotalAnswersCount(cardId: Long, deckId: Long, source: Source): Int
}