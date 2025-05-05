package com.example.training.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.common.domain.entity.Card
import com.example.database.TPrepDatabase
import com.example.database.models.AnswerStatsDBO
import com.example.database.models.CorrectAnswerDBO
import com.example.database.models.ErrorAnswerDBO
import com.example.database.models.HistoryDBO
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.network.api.ApiService
import com.example.preferences.auth.AuthPreferences
import com.example.preferences.auth.util.AuthRequestWrapper
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
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random


class TrainingRepositoryImpl @Inject constructor(
    private val context: Context,
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val preferences: AuthPreferences,
    private val authRequestWrapper: AuthRequestWrapper,
) : TrainingRepository {

    override suspend fun prepareTrainingCards(
        deckId: String,
        cards: List<Card>,
        modes: Set<TrainingMode>,
    ): List<TrainingCard> {
        require(modes.isNotEmpty()) { "At least one training mode must be selected" }

        return withContext(Dispatchers.IO) {
            val userId = preferences.getUserId()
                ?: throw IllegalStateException("User ID not found in preferences")

            // Получение статистики по каждой карте
            val cardsWithPriority = cards.map { card ->
                val answerStats = database.historyDao.getAnswerStatsForCard(
                    cardId = card.id,
                    deckId = deckId,
                    userId = userId
                )
                val lastAnswerCorrect = database.historyDao.wasLastAnswerCorrect(
                    cardId = card.id,
                    deckId = deckId,
                    userId = userId
                )

                val isNew = answerStats.correctCount == 0 && answerStats.errorCount == 0
                val coefficient =
                    if (isNew) DEFAULT_COEFFICIENT else calculateCoefficientFromHistory(answerStats)

                val priority = when {
                    isNew -> NEW_CARD_PRIORITY
                    !lastAnswerCorrect -> EXISTING_CARD_PRIORITY
                    coefficient < STABLE_CARD_COEFFICIENT_THRESHOLD -> coefficient
                    else -> Random.nextDouble() + STABLE_CARD_COEFFICIENT_THRESHOLD
                }

                Pair(card, priority)
            }

            // Сортировка карт по приоритету
            val sortedCards = cardsWithPriority
                .sortedBy { it.second }
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
                attachment = card.attachment,
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
                    attachment = card.attachment,
                )
            }

            TrainingMode.FILL_IN_THE_BLANK -> {
                val (partialAnswer, missingWords, startIndex) = generatePartialAnswer(card.answer)
                TrainingCard(
                    id = card.id,
                    trainingMode = mode,
                    question = card.question,
                    answer = card.answer,
                    partialAnswer = partialAnswer,
                    missingWords = missingWords,
                    missingWordStartIndex = startIndex,
                    attachment = card.attachment,
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
        if (currentCard.wrongAnswers.isNotEmpty()) {
            return currentCard.wrongAnswers
        }

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
        cardId: Int,
        question: String,
        answer: String,
        blankAnswer: String?,
        userAnswer: String?,
        isCorrect: Boolean,
        trainingSessionId: String,
        trainingMode: TrainingMode,
        attachment: String?,
    ) {
        if (isCorrect) {
            val correctAnswer = CorrectAnswerDBO(
                id = DEFAULT_CORRECT_ANSWER_ID,
                cardId = cardId,
                trainingMode = trainingMode,
                trainingSessionId = trainingSessionId
            )
            database.correctAnswerDao.insertCorrectAnswer(correctAnswer)
        } else if (userAnswer != null) {
            val errorAnswer = ErrorAnswerDBO(
                id = DEFAULT_ERROR_ANSWER_ID,
                trainingSessionId = trainingSessionId,
                cardId = cardId,
                question = question,
                answer = answer,
                userAnswer = userAnswer,
                blankAnswer = blankAnswer,
                trainingMode = trainingMode,
                attachment = attachment
            )
            database.errorDao.insertError(errorAnswer)
        }
    }

    override suspend fun getCardPicture(
        deckId: String,
        cardId: Int,
        source: Source,
        attachment: String?,
    ): Uri? {
        return when (source) {
            Source.LOCAL -> {
                return try {
                    val card = database.cardDao.getCardById(cardId)
                    val path = card?.picturePath ?: return null
                    File(path).toUri()
                } catch (_: Exception) {
                    null
                }
            }

            Source.NETWORK -> {
                if (attachment.isNullOrBlank()) {
                    return null
                }

                authRequestWrapper.executeWithAuth { token ->
                    val response = apiService.getCardPicture(
                        deckId = deckId,
                        cardId = cardId,
                        objectName = attachment,
                        authHeader = token
                    )

                    when {
                        response.isSuccessful -> {
                            response.body()?.let { body ->
                                val tempFile = File(context.cacheDir, "card_temp_$cardId.jpg")
                                tempFile.outputStream().use { output ->
                                    body.byteStream().copyTo(output)
                                }
                                tempFile.toUri()
                            }
                        }

                        response.code() == 404 -> {
                            throw IOException("Card picture not found")
                        }

                        else -> {
                            null
                        }
                    }
                }
            }
        }
    }

    override suspend fun recordTraining(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        source: Source,
        trainingSessionId: String,
    ) {
        val userId = preferences.getUserId()
            ?: throw IllegalStateException("User ID not found in preferences")

        // Проверяем, существует ли уже запись с данным trainingSessionId
        val existingHistory = database.historyDao.getHistoryByTrainingSessionId(trainingSessionId)

        // Если запись существует, обновляем её, иначе вставляем новую
        val history = HistoryDBO(
            id = existingHistory?.id ?: DEFAULT_HISTORY_ID,
            userId = userId,
            deckId = deckId,
            deckName = deckName,
            cardsCount = cardsCount,
            timestamp = System.currentTimeMillis(),
            trainingSessionId = trainingSessionId,
            source = source,
            isSynchronized = false
        )
        if (existingHistory != null) {
            database.historyDao.updateHistory(history)
        } else {
            database.historyDao.insertHistory(history)
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

    override suspend fun getDeckNameAndTrainingSessionTime(trainingSessionId: String): Triple<String, Long, Source> {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)
            ?: throw IllegalStateException("History with trainingSessionId: $trainingSessionId is not find.")

        return Triple(trainingHistory.deckName, trainingHistory.timestamp, trainingHistory.source)
    }

    override suspend fun getTotalAndCorrectCountAnswers(trainingSessionId: String): Pair<Int, Int> {
        val answerStats = database.historyDao.getAnswerStatsForSession(trainingSessionId)
        val totalAnswers = answerStats.correctCount + answerStats.errorCount
        return Pair(totalAnswers, answerStats.correctCount)
    }

    override suspend fun getNextTrainingTime(trainingSessionId: String): Long? {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)

        val deckId = trainingHistory?.deckId
            ?: throw IllegalStateException("History with trainingSessionId: $trainingSessionId is not find.")

        return database.trainingReminderDao.getNextReminder(deckId)?.reminderTime
    }

    override suspend fun getErrorsList(trainingSessionId: String): List<TrainingError> {
        val trainingErrors = database.errorDao.getErrorsForTrainingSession(trainingSessionId)

        return trainingErrors.map { it.toEntity() }
    }

    override suspend fun getInfoForNavigationToDeck(trainingSessionId: String): Pair<String, Source> {
        val trainingHistory = database.historyDao.getHistoryForTrainingSession(trainingSessionId)
            ?: throw IllegalStateException("History with trainingSessionId: $trainingSessionId is not find.")

        return trainingHistory.deckId to trainingHistory.source
    }

    private fun calculateCoefficientFromHistory(answerStats: AnswerStatsDBO): Double {
        var coefficient = DEFAULT_COEFFICIENT
        repeat(answerStats.correctCount) { coefficient += COEFFICIENT_INCREMENT }
        repeat(answerStats.errorCount) { coefficient += COEFFICIENT_DECREMENT }
        return coefficient.coerceIn(MIN_COEFFICIENT, MAX_COEFFICIENT)
    }

    companion object {

        private const val NEW_CARD_PRIORITY = 0.0
        private const val EXISTING_CARD_PRIORITY = 1.0
        private const val DEFAULT_COEFFICIENT = 2.5
        private const val MIN_COEFFICIENT = 1.3
        private const val MAX_COEFFICIENT = 2.5
        private const val STABLE_CARD_COEFFICIENT_THRESHOLD = 2.0
        private const val COEFFICIENT_INCREMENT = 0.1
        private const val COEFFICIENT_DECREMENT = -0.2
        private const val WRONG_ANSWERS_COUNT = 3
        private const val MIN_INPUT_LENGTH_PERCENT = 0.5
        private const val MAX_ALLOWED_ERROR_PERCENT = 0.2
        private const val DEFAULT_HISTORY_ID = 0L
        private const val DEFAULT_CORRECT_ANSWER_ID = 0L
        private const val DEFAULT_ERROR_ANSWER_ID = 0L
    }
}