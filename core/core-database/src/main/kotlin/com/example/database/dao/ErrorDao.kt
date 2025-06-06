package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.ErrorAnswerDBO
import com.example.database.models.ErrorAnswerWithTimeDBO

@Dao
interface ErrorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertError(error: ErrorAnswerDBO)

    @Query("""
    SELECT e.*, h.timestamp AS trainingSessionTime, h.deckId 
    FROM error_answer e
    JOIN history h ON e.trainingSessionId = h.trainingSessionId
    WHERE e.trainingSessionId = :trainingSessionId
""")
    suspend fun getErrorsForTrainingSession(trainingSessionId: String): List<ErrorAnswerWithTimeDBO>

    @Query("SELECT * FROM error_answer WHERE trainingSessionId =:trainingSessionId")
    suspend fun getErrorAnswersForTrainingSession(trainingSessionId: String): List<ErrorAnswerDBO>
}