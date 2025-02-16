package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.TrainingModesHistoryDBO

@Dao
interface TrainingModesHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrainingModes(trainingModesHistory: TrainingModesHistoryDBO)

    @Query("SELECT * FROM training_modes_history WHERE deckId = :deckId")
    suspend fun getTrainingModes(deckId: String): TrainingModesHistoryDBO?

    @Query("DELETE FROM training_modes_history WHERE deckId = :deckId")
    suspend fun deleteTrainingModes(deckId: String)
}