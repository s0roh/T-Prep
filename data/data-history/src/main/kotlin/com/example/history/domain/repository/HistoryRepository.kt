package com.example.history.domain.repository

import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.entity.TrainingModeStats

interface HistoryRepository {

    suspend fun getTrainingHistory(): List<TrainingHistoryItem>

    suspend fun getTrainingStats(): Pair<Int, Int>

    suspend fun getDeckTrainingStats(deckId: String): List<Double>

    suspend fun getDeckTrainingModeStats(deckId: String): List<TrainingModeStats>
}