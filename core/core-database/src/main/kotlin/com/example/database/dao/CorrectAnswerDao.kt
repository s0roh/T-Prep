package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.CorrectAnswerDBO

@Dao
interface CorrectAnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCorrectAnswer(correctAnswer: CorrectAnswerDBO)

    @Query("SELECT * FROM correct_answer WHERE trainingSessionId =:trainingSessionId")
    suspend fun getCorrectAnswersForTrainingSession(trainingSessionId: String): List<CorrectAnswerDBO>
}