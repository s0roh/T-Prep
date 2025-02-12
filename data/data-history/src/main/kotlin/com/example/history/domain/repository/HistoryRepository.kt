package com.example.history.domain.repository

import com.example.history.domain.entity.TrainingHistoryItem

interface HistoryRepository {

    suspend fun getTrainingHistory(): List<TrainingHistoryItem>
}