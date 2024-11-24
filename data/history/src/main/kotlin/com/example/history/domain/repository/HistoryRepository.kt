package com.example.history.domain.repository

import com.example.database.models.Source
import com.example.history.domain.entity.HistoryGroup
import com.example.history.domain.entity.TrainingHistory

interface HistoryRepository {

    suspend fun getLastTrainingPerDeck(): List<TrainingHistory>

    suspend fun insertHistory(history: TrainingHistory, source: Source)

    suspend fun getGroupedHistory(): List<HistoryGroup>
}