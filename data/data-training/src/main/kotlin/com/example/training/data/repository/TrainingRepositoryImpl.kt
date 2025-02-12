package com.example.training.data.repository

import com.example.common.domain.entity.Card
import com.example.database.TPrepDatabase
import com.example.database.models.ErrorDBO
import com.example.database.models.HistoryDBO
import com.example.database.models.Source
import com.example.preferences.AuthPreferences
import com.example.training.data.mapper.toEntity
import com.example.training.domain.entity.TrainingError
import com.example.training.domain.repository.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrainingRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase,
    private val preferences: AuthPreferences,
) : TrainingRepository {

    override suspend fun prepareTrainingCards(
        deckId: String,
        cards: List<Card>,
        source: Source,
    ): List<Card> {
        return withContext(Dispatchers.IO) {
            val userId = preferences.getUserId()
                ?: throw IllegalStateException("User ID not found in preferences")
            val cardsWithSortingData = cards.map { card ->
                val historyList = database.historyDao.getHistoryForCard(
                    cardId = card.id,
                    deckId = deckId,
                    source = source,
                    userId = userId
                )
                val isNew = historyList.isEmpty()
                val coefficient =
                    if (isNew) DEFAULT_COEFFICIENT else calculateCoefficientFromHistory(historyList)
                card to Pair(isNew, coefficient)
            }

            val sortedCards = cardsWithSortingData
                .sortedWith(compareBy(
                    { if (it.second.first) NEW_CARD_PRIORITY else EXISTING_CARD_PRIORITY },
                    { it.second.second }
                ))
                .map { it.first }

            sortedCards.map { card ->
                val wrongAnswers = generateWrongAnswers(card, sortedCards)
                card.copy(wrongAnswers = wrongAnswers)
            }
        }
    }

    private fun generateWrongAnswers(
        currentCard: Card,
        allCards: List<Card>,
    ): List<String> {
        return allCards
            .asSequence()
            .filter { it.id != currentCard.id }
            .map { it.answer }
            .filter { it != currentCard.answer }
            .toSet()
            .shuffled()
            .take(WRONG_ANSWERS_COUNT)
    }

    override suspend fun recordAnswer(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        cardId: Int,
        isCorrect: Boolean,
        incorrectAnswer: String?,
        source: Source,
        trainingSessionId: String,
    ) {
        val userId = preferences.getUserId()
            ?: throw IllegalStateException("User ID not found in preferences")

        val history = HistoryDBO(
            id = 0,
            deckId = deckId,
            deckName = deckName,
            cardsCount = cardsCount,
            cardId = cardId,
            timestamp = System.currentTimeMillis(),
            isCorrect = isCorrect,
            source = source,
            userId = userId,
            trainingSessionId = trainingSessionId
        )

        database.historyDao.insertHistory(history)

        if (!isCorrect && incorrectAnswer != null) {
            val error = ErrorDBO(
                id = 0,
                trainingSessionId = trainingSessionId,
                deckId = deckId,
                cardId = cardId,
                incorrectAnswer = incorrectAnswer
            )
            database.errorDao.insertError(error)
        }
    }

    override suspend fun getDeckNameAndTrainingSessionTime(trainingSessionId: String): Pair<String, Long> {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)

        val deckName = trainingHistory.firstOrNull()?.deckName
            ?: throw IllegalStateException("Deck name is not available.")

        val trainingSessionTime = trainingHistory.maxByOrNull { it.timestamp }
            ?.timestamp
            ?: throw IllegalStateException("No training session found.")

        return Pair(deckName, trainingSessionTime)
    }

    override suspend fun getTotalAndCorrectCountAnswers(trainingSessionId: String): Pair<Int, Int> {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)
        val totalAnswers = trainingHistory.size
        val correctAnswers = trainingHistory.count { it.isCorrect }
        return Pair(totalAnswers, correctAnswers)
    }

    override suspend fun getNextTrainingTime(trainingSessionId: String): Long? {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)

        val deckId = trainingHistory.first().deckId
        val source = trainingHistory.first().source

        return database.trainingReminderDao.getNextReminder(deckId, source)?.reminderTime
    }

    override suspend fun getErrorsList(trainingSessionId: String): List<TrainingError> {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)
        val trainingSessionTime =
            trainingHistory.maxByOrNull { it.timestamp }?.timestamp ?: return emptyList()

        val trainingErrors = database.errorDao.getErrorsForTrainingSession(trainingSessionId)

        return trainingErrors.mapNotNull { error ->
            val card = database.cardDao.getCardById(error.cardId) ?: return@mapNotNull null
            val question = card.question
            val correctAnswer = card.answer

            error.toEntity(trainingSessionTime, question, correctAnswer)
        }
    }

    override suspend fun getInfoForNavigationToDeck(trainingSessionId: String): Pair<String, Source> {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)
        val firstEntry = trainingHistory.firstOrNull()
            ?: throw IllegalStateException("No history found for session $trainingSessionId")

        return firstEntry.deckId to firstEntry.source
    }

    private fun calculateCoefficientFromHistory(historyList: List<HistoryDBO>): Double {
        var coefficient = DEFAULT_COEFFICIENT
        historyList.forEach {
            val adjustment = if (it.isCorrect) COEFFICIENT_INCREMENT else COEFFICIENT_DECREMENT
            coefficient += adjustment
        }
        return coefficient.coerceIn(MIN_COEFFICIENT, MAX_COEFFICIENT)
    }

    companion object {

        const val NEW_CARD_PRIORITY = 0
        const val EXISTING_CARD_PRIORITY = 1
        const val DEFAULT_COEFFICIENT = 2.5
        const val MIN_COEFFICIENT = 1.3
        const val MAX_COEFFICIENT = 2.5
        const val COEFFICIENT_INCREMENT = 0.1
        const val COEFFICIENT_DECREMENT = -0.2
        const val WRONG_ANSWERS_COUNT = 3
    }
}