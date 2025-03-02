package com.example.history.data.repository

import com.example.database.TPrepDatabase
import com.example.history.data.mapper.toEntity
import com.example.history.domain.entity.TrainingHistory
import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.repository.HistoryRepository
import com.example.preferences.AuthPreferences
import javax.inject.Inject

class HistoryRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase,
    private val preferences: AuthPreferences,
) : HistoryRepository {

    override suspend fun getTrainingHistory(): List<TrainingHistoryItem> {
        val allTrainings = getAllTrainingHistories()
        val currentTime = System.currentTimeMillis()

        return allTrainings
            .groupBy { it.trainingSessionId }
            .map { (_, trainings) ->
                // Получаем timestamp последней карточки в сессии
                val lastTraining = trainings.maxByOrNull { it.timestamp }
                val timestamp = lastTraining?.timestamp ?: currentTime

                // Подсчитываем процент правильных ответов в сессии
                val percentOfCorrectAnswers = calculatePercentOfCorrectAnswers(trainings)

                TrainingHistoryItem(
                    timestamp = timestamp,
                    percentOfCorrectAnswers = percentOfCorrectAnswers,
                    trainingHistories = trainings
                )
            }
    }

    private suspend fun getAllTrainingHistories(): List<TrainingHistory> {
        val userId = preferences.getUserId()
            ?: throw IllegalStateException("User ID not found in preferences")

        return database.historyDao.getAllTrainingHistories(userId).map { it.toEntity() }
    }

    // Функция для подсчета процента правильных ответов в пределах одной сессии
    private fun calculatePercentOfCorrectAnswers(trainingHistories: List<TrainingHistory>): Int {
        val totalAnswers = trainingHistories.size
        val correctAnswers = trainingHistories.count { it.isCorrect }
        return if (totalAnswers > 0) {
            (correctAnswers * 100) / totalAnswers
        } else {
            0
        }
    }
}