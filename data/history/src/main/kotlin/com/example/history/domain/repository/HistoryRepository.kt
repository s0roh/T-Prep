package com.example.history.domain.repository

import com.example.database.models.Source
import com.example.history.domain.entity.TrainingHistory

interface HistoryRepository {

    suspend fun getAllHistory(): List<TrainingHistory>

    suspend fun insertHistory(history: TrainingHistory, source: Source)
}