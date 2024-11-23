package com.example.history.data.repository

import com.example.database.TPrepDatabase
import com.example.database.models.Source
import com.example.history.data.mapper.toDBO
import com.example.history.data.mapper.toEntity
import com.example.history.domain.entity.TrainingHistory
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val database: TPrepDatabase
) : com.example.history.domain.repository.HistoryRepository {

    override suspend fun getAllHistory(): List<TrainingHistory> {
        return database.historyDao.getAllHistory().map { it.toEntity() }
    }

    override suspend fun insertHistory(
        history: TrainingHistory,
        source: Source
    ) {
        return database.historyDao.insertHistory(history.toDBO(source))
    }
}