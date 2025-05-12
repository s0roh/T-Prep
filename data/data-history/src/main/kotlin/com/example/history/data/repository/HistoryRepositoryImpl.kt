package com.example.history.data.repository

import com.example.database.TPrepDatabase
import com.example.database.models.TrainingMode
import com.example.history.data.mapper.toEntity
import com.example.history.domain.entity.TrainingHistory
import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.entity.TrainingModeStats
import com.example.history.domain.repository.HistoryRepository
import com.example.preferences.auth.AuthPreferences
import javax.inject.Inject

class HistoryRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase,
    private val preferences: AuthPreferences,
) : HistoryRepository {

    override suspend fun getTrainingHistory(): List<TrainingHistoryItem> {
        val allTrainings = getAllTrainingHistories()

        return allTrainings
            .groupBy { it.trainingSessionId }
            .map { (trainingSessionId, trainings) ->
                // Подсчитываем процент правильных ответов в сессии
                val percentOfCorrectAnswers = calculatePercentOfCorrectAnswers(trainingSessionId)

                TrainingHistoryItem(
                    timestamp = trainings.first().timestamp,
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
            groupedTrainings.values.sumOf {
                calculatePercentOfCorrectAnswers(
                    it.first().trainingSessionId,
                    true
                )
            } / totalTrainings
        } else {
            0
        }

        return totalTrainings to averageAccuracy
    }

    override suspend fun getDeckTrainingStats(deckId: String): List<Double> {
        val allTrainings = getDeckTrainingHistories(deckId)
        if (allTrainings.isEmpty()) return emptyList()

        return allTrainings
            .groupBy { it.trainingSessionId }
            .mapNotNull { (_, trainings) ->
                val cardsCount = trainings.firstOrNull()?.cardsCount ?: return@mapNotNull null
                if (cardsCount == 0) return@mapNotNull null

                val correctAnswers = trainings.count { it.isCorrect }
                ((correctAnswers.toDouble() / cardsCount) * 100).coerceAtMost(100.0)
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

        // Получаем истории для конкретной колоды
        val historyRecords = database.historyDao.getHistoryForDeck(deckId = deckId, userId = userId)

        // Преобразуем каждую запись в TrainingHistory
        return historyRecords.flatMap { history ->
            // Получаем ошибки и правильные ответы для каждой сессии
            val errorAnswers =
                database.errorDao.getErrorAnswersForTrainingSession(history.trainingSessionId)
            val correctAnswers =
                database.correctAnswerDao.getCorrectAnswersForTrainingSession(history.trainingSessionId)

            // Преобразуем ошибки в TrainingHistory
            val historyWithErrors = errorAnswers.map { error ->
                history.toEntity(
                    isCorrect = false,  // Ошибочные ответы
                    trainingMode = error.trainingMode,
                    cardId = error.cardId
                )
            }

            // Преобразуем правильные ответы в TrainingHistory
            val historyWithCorrectAnswers = correctAnswers.map { correct ->
                history.toEntity(
                    isCorrect = true,  // Правильные ответы
                    trainingMode = correct.trainingMode,
                    cardId = correct.cardId
                )
            }

            // Объединяем результаты ошибок и правильных ответов
            historyWithErrors + historyWithCorrectAnswers
        }
    }

    private suspend fun getAllTrainingHistories(): List<TrainingHistory> {
        val userId = preferences.getUserId()
            ?: throw IllegalStateException("User ID not found in preferences")

        val historyRecords = database.historyDao.getAllTrainingHistories(userId)

        return historyRecords.flatMap { history ->
            val errorAnswers =
                database.errorDao.getErrorAnswersForTrainingSession(history.trainingSessionId)
            val correctAnswers =
                database.correctAnswerDao.getCorrectAnswersForTrainingSession(history.trainingSessionId)

            // Преобразуем ошибки в TrainingHistory
            val historyWithErrors = errorAnswers.map { error ->
                history.toEntity(
                    isCorrect = false,  // Ошибочные ответы
                    trainingMode = error.trainingMode,
                    cardId = error.cardId
                )
            }

            // Преобразуем правильные ответы в TrainingHistory
            val historyWithCorrectAnswers = correctAnswers.map { correct ->
                history.toEntity(
                    isCorrect = true,  // Правильные ответы
                    trainingMode = correct.trainingMode,
                    cardId = correct.cardId
                )
            }

            // Объединяем результаты ошибок и правильных ответов
            historyWithErrors + historyWithCorrectAnswers
        }
    }

    // Функция для подсчета процента правильных ответов в пределах одной сессии
    private suspend fun calculatePercentOfCorrectAnswers(
        trainingSessionId: String,
        useTotalDeckSize: Boolean = false,
    ): Int {
        val answerStats = database.historyDao.getAnswerStatsForSession(trainingSessionId)

        // Выбираем, от какого количества считать процент: от всех карт или от количества ответов
        val totalAnswers = if (useTotalDeckSize) {
            answerStats.allCount ?: 0
        } else {
            answerStats.correctCount + answerStats.errorCount
        }

        if (totalAnswers == 0) return 0

        return (answerStats.correctCount * 100) / totalAnswers
    }
}