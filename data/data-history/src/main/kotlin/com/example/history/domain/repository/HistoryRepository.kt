package com.example.history.domain.repository

import com.example.history.domain.entity.HistoryWithTimePeriod
import com.example.history.domain.entity.TrainingHistory

interface HistoryRepository {

    suspend fun getLastTrainingPerDeck(): List<TrainingHistory>

    suspend fun insertHistory(history: TrainingHistory)

    suspend fun getGroupedHistory(): List<HistoryWithTimePeriod>
}