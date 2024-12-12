package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.Source
import com.example.database.models.TrainingReminderDBO

@Dao
interface TrainingReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(trainingReminder: TrainingReminderDBO): Long

    @Query("SELECT * FROM training_reminders WHERE id = :reminderId LIMIT 1")
    suspend fun getReminderById(reminderId: Long): TrainingReminderDBO?

    @Query("SELECT * FROM training_reminders WHERE deckId = :deckId AND source = :source LIMIT 1")
    suspend fun getReminder(deckId: String, source: Source): TrainingReminderDBO?

    @Query("SELECT * FROM training_reminders")
    suspend fun getAllReminders(): List<TrainingReminderDBO>

    @Query("DELETE FROM training_reminders WHERE deckId = :deckId AND source = :source")
    suspend fun deleteReminder(deckId: String, source: Source)
}