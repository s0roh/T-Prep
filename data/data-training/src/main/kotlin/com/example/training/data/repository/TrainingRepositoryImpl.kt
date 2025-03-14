package com.example.training.data.repository

import com.example.common.domain.entity.Card
import com.example.database.TPrepDatabase
import com.example.database.models.ErrorDBO
import com.example.database.models.HistoryDBO
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.preferences.AuthPreferences
import com.example.training.data.mapper.toDbo
import com.example.training.data.mapper.toEntity
import com.example.training.data.util.generatePartialAnswer
import com.example.training.data.util.levenshteinDistance
import com.example.training.data.util.normalizeText
import com.example.training.domain.entity.TrainingCard
import com.example.training.domain.entity.TrainingError
import com.example.training.domain.entity.TrainingModes
import com.example.training.domain.repository.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max


class TrainingRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase,
    private val preferences: AuthPreferences,
) : TrainingRepository {

    override suspend fun prepareTrainingCards(
        deckId: String,
        cards: List<Card>,
        source: Source,
        modes: Set<TrainingMode>,
    ): List<TrainingCard> {
        require(modes.isNotEmpty()) { "At least one training mode must be selected" }

        return withContext(Dispatchers.IO) {
            val userId = preferences.getUserId()
                ?: throw IllegalStateException("User ID not found in preferences")

            // Сортировка карт по истории и коэффициентам
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

            // Сортировка карт по приоритету и коэффициенту
            val sortedCards = cardsWithSortingData
                .sortedWith(
                    compareBy(
                    { if (it.second.first) NEW_CARD_PRIORITY else EXISTING_CARD_PRIORITY },
                    { it.second.second }
                ))
                .map { it.first }

            // Применение выбранного режима тренировки к каждой карте
            sortedCards.map { card ->
                val selectedMode = modes.random() // Выбираем случайный режим
                assignModeSpecificFields(card, selectedMode, sortedCards)
            }
        }
    }

    private fun assignModeSpecificFields(
        card: Card,
        mode: TrainingMode,
        allCards: List<Card>,
    ): TrainingCard {
        return when (mode) {
            TrainingMode.MULTIPLE_CHOICE -> TrainingCard(
                id = card.id,
                trainingMode = mode,
                question = card.question,
                answer = card.answer,
                wrongAnswers = generateWrongAnswers(card, allCards)
            )

            TrainingMode.TRUE_FALSE -> {
                val isCorrect = (0..1).random() == 1
                val displayedAnswer = if (isCorrect) card.answer else generateWrongAnswers(
                    card,
                    allCards
                ).firstOrNull() ?: card.answer
                TrainingCard(
                    id = card.id,
                    trainingMode = mode,
                    question = card.question,
                    answer = card.answer,
                    displayedAnswer = displayedAnswer,
                )
            }

            TrainingMode.FILL_IN_THE_BLANK -> {
                val (partialAnswer, missingWords) = generatePartialAnswer(card.answer)
                TrainingCard(
                    id = card.id,
                    trainingMode = mode,
                    question = card.question,
                    answer = card.answer,
                    partialAnswer = partialAnswer,
                    missingWords = missingWords
                )
            }
        }
    }

    override suspend fun checkFillInTheBlankAnswer(
        userInput: String,
        correctWords: List<String>,
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val normalizedInput = normalizeText(userInput)
            val normalizedCorrectText = normalizeText(correctWords.joinToString(" "))

            // Если correctWords содержит только одно слово, проверяем полное соответствие
            if (correctWords.size == 1) {
                return@withContext normalizedInput == normalizedCorrectText
            }

            if (normalizedInput.length < normalizedCorrectText.length * MIN_INPUT_LENGTH_PERCENT) {
                return@withContext false
            }

            val maxAllowedErrors =
                max(1, (normalizedCorrectText.length * MAX_ALLOWED_ERROR_PERCENT).toInt())

            // Проверяем расстояние Левенштейна
            levenshteinDistance(normalizedInput, normalizedCorrectText) <= maxAllowedErrors
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
        question: String,
        correctAnswer: String,
        fillInTheBlankAnswer: String?,
        incorrectAnswer: String?,
        source: Source,
        trainingSessionId: String,
        trainingMode: TrainingMode,
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
            trainingMode = trainingMode,
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
                question = question,
                correctAnswer = correctAnswer,
                fillInTheBlankAnswer = fillInTheBlankAnswer,
                incorrectAnswer = incorrectAnswer,
                trainingMode = trainingMode
            )
            database.errorDao.insertError(error)
        }
    }

    override suspend fun saveTrainingModes(trainingModes: TrainingModes) {
        database.trainingModesHistoryDao.saveTrainingModes(trainingModes.toDbo())
    }

    override suspend fun getTrainingModes(deckId: String): TrainingModes {
        return database.trainingModesHistoryDao.getTrainingModes(deckId)?.toEntity()
            ?: TrainingModes(
                deckId,
                TrainingMode.entries
            )
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

        return trainingErrors.map { error ->
            error.toEntity(trainingSessionTime)
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

        private const val NEW_CARD_PRIORITY = 0
        private const val EXISTING_CARD_PRIORITY = 1
        private const val DEFAULT_COEFFICIENT = 2.5
        private const val MIN_COEFFICIENT = 1.3
        private const val MAX_COEFFICIENT = 2.5
        private const val COEFFICIENT_INCREMENT = 0.1
        private const val COEFFICIENT_DECREMENT = -0.2
        private const val WRONG_ANSWERS_COUNT = 3
        private const val MIN_INPUT_LENGTH_PERCENT = 0.5
        private const val MAX_ALLOWED_ERROR_PERCENT = 0.2
    }
}