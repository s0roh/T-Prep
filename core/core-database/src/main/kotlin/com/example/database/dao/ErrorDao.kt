package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.ErrorDBO

@Dao
interface ErrorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertError(error: ErrorDBO)

    @Query("SELECT * FROM errors WHERE trainingSessionId = :trainingSessionId")
    suspend fun getErrorsForTrainingSession(trainingSessionId: String): List<ErrorDBO>

    @Query("DELETE FROM errors WHERE deckId = :deckId")
    suspend fun deleteErrorForDeck(deckId: String)
}