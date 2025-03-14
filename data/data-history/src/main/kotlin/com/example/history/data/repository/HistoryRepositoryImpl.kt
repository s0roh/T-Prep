package com.example.history.data.repository

import com.example.database.TPrepDatabase
import com.example.database.models.TrainingMode
import com.example.history.data.mapper.toEntity
import com.example.history.domain.entity.TrainingHistory
import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.entity.TrainingModeStats
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

    override suspend fun getTrainingStats(): Pair<Int, Int> {
        val allTrainings = getAllTrainingHistories()
        val groupedTrainings = allTrainings.groupBy { it.trainingSessionId }

        val totalTrainings = groupedTrainings.size

        val averageAccuracy = if (totalTrainings > 0) {
            groupedTrainings.values.sumOf { calculatePercentOfCorrectAnswers(it) } / totalTrainings
        } else {
            0
        }

        return totalTrainings to averageAccuracy
    }

    override suspend fun getDeckTrainingStats(deckId: String): List<Double> {
        val allTrainings = getDeckTrainingHistories(deckId)
        val groupedTrainings = allTrainings.groupBy { it.trainingSessionId }

        return groupedTrainings.map { (_, trainings) ->
            val totalQuestions = trainings.size
            val correctAnswers = trainings.count { it.isCorrect }
            if (totalQuestions > 0) (correctAnswers.toDouble() / totalQuestions) * 100 else 0.0
        }
    }

    override suspend fun getDeckTrainingModeStats(deckId: String): List<TrainingModeStats> {
        val allTrainings = getDeckTrainingHistories(deckId)

        val modeStatsMap = mutableMapOf<TrainingMode, TrainingModeStats>()

        allTrainings.forEach { training ->
            val mode = training.trainingMode

            val stats = modeStatsMap.getOrPut(mode) {
                TrainingModeStats(mode, 0.0, 0.0, 0.0)
            }

            val updatedStats = stats.copy(
                totalAttempts = stats.totalAttempts + 1,
                correctAttempts = if (training.isCorrect) stats.correctAttempts + 1 else stats.correctAttempts,
                incorrectAttempts = if (!training.isCorrect) stats.incorrectAttempts + 1 else stats.incorrectAttempts
            )

            modeStatsMap[mode] = updatedStats
        }
        return modeStatsMap.values.sortedBy { it.modeName.ordinal }
    }

    private suspend fun getDeckTrainingHistories(deckId: String): List<TrainingHistory> {
        val userId = preferences.getUserId()
            ?: throw IllegalStateException("User ID not found in preferences")

        return database.historyDao.getHistoryForDeck(deckId = deckId, userId = userId)
            .map { it.toEntity() }
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