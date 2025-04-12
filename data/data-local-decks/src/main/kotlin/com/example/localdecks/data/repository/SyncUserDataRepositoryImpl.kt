package com.example.localdecks.data.repository

import android.content.Context
import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.CardDBO
import com.example.database.models.CorrectAnswerDBO
import com.example.database.models.DeckDBO
import com.example.database.models.ErrorAnswerDBO
import com.example.database.models.HistoryDBO
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.localdecks.domain.repository.SyncUserDataRepository
import com.example.network.api.ApiService
import com.example.network.dto.collection.history.CorrectAnswerDto
import com.example.network.dto.collection.history.ErrorAnswerDto
import com.example.network.dto.collection.history.HistoryItemDto
import com.example.network.dto.global.CardDto
import com.example.preferences.AuthPreferences
import com.example.preferences.AuthRequestWrapper
import kotlinx.coroutines.flow.first
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class SyncUserDataRepositoryImpl @Inject constructor(
    private val context: Context,
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
    private val preferences: AuthPreferences,
) : SyncUserDataRepository {

    override suspend fun syncUserData() {
        authRequestWrapper.executeWithAuth { token ->
            if (token.isNullOrEmpty()) {
                Log.e(TAG, "Токен отсутствует или пустой.")
                return@executeWithAuth
            }

            val response = apiService.getUserInfo(authHeader = token)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody == null) {
                    Log.e(TAG, "Ответ от сервера пустой.")
                    return@executeWithAuth
                }

                val userId = responseBody.userId
                val userName = responseBody.userName
                val userEmail = responseBody.email
                val serverDeckIds = responseBody.collectionsId

                preferences.saveUserId(userId)
                preferences.saveUserName(userName)
                userEmail?.let { preferences.saveUserEmail(userEmail) }

                syncDecksWithServer(userId, serverDeckIds, token)
                syncTrainingHistoryWithServer(userId = userId, token = token)
            } else {
                Log.e(
                    TAG,
                    "Ошибка при получении информации о пользователе: ${
                        response.errorBody()?.string()
                    }"
                )
            }
        }
    }

    private suspend fun syncDecksWithServer(
        userId: String,
        serverDeckIds: List<String>,
        token: String,
    ) {
        val allDecks = database.deckDao.getAllDecks().first()
        val serverDeckIdsSet = serverDeckIds.toSet()

        val userDecks = allDecks.filter { it.userId == userId }
        val nonUserDecks = allDecks.filter { it.userId != userId }

        handleUserDecks(userDecks, serverDeckIdsSet)
        handleNonUserDecks(nonUserDecks)

        serverDeckIds.forEach { deckId ->
            syncDeckWithServer(deckId, userId, token)
        }
    }

    private suspend fun handleUserDecks(userDecks: List<DeckDBO>, serverDeckIdsSet: Set<String>) {
        userDecks.forEach { localDeck ->
            if (localDeck.serverDeckId in serverDeckIdsSet) {
                if (localDeck.isDeleted) {
                    Log.d(TAG, "Восстанавливаем колоду пользователя: $localDeck")
                    database.deckDao.updateDeck(localDeck.copy(isDeleted = false))
                }
            } else {
                Log.d(TAG, "Удаляем локальную колоду и её содержимое: $localDeck")

                val cards = database.cardDao.getCardsForDeck(localDeck.id)
                cards.first().forEach { card ->
                    card.picturePath?.let { path ->
                        val file = File(path)
                        if (file.exists()) file.delete()
                    }
                    database.cardDao.deleteCard(card)
                }
                database.deckDao.deleteDeck(localDeck)
            }
        }
    }

    private suspend fun handleNonUserDecks(nonUserDecks: List<DeckDBO>) {
        nonUserDecks.forEach { localDeck ->
            if (!localDeck.isDeleted) {
                Log.d(TAG, "Скрываем колоду: $localDeck")
                database.deckDao.updateDeck(localDeck.copy(isDeleted = true))
            }
        }
    }

    private suspend fun syncDeckWithServer(deckId: String, userId: String, token: String) {
        val deckDto = apiService.getDeckById(deckId = deckId, authHeader = token)
        val existingDeck = database.deckDao.getDeckByServerId(deckId)

        val deckLocalId = if (existingDeck != null) {
            if (existingDeck.name != deckDto.name || existingDeck.isPublic != deckDto.isPublic) {
                Log.d(TAG, "Обновление локальной колоды. Старое: $existingDeck, новое: $deckDto")
                database.deckDao.updateDeck(
                    existingDeck.copy(
                        name = deckDto.name,
                        isPublic = deckDto.isPublic
                    )
                )
            }
            existingDeck.id
        } else {
            Log.d(TAG, "Добавление новой колоды: $deckDto")
            database.deckDao.insertDeck(
                DeckDBO(
                    serverDeckId = deckId,
                    name = deckDto.name,
                    isPublic = deckDto.isPublic,
                    userId = userId
                )
            ).toString()
        }

        syncCardsWithServer(deckDto.cards, deckLocalId, deckId)
    }

    private suspend fun syncCardsWithServer(
        cards: List<CardDto>,
        localDeckId: String,
        serverDeckId: String,
    ) {
        val localCards = database.cardDao.getCardsForDeck(localDeckId).first()
        val serverCardIds = cards.map { it.id }

        cards.forEach { cardDto ->
            val existingCard = database.cardDao.getCardByServerId(
                serverCardId = cardDto.id,
                deckId = localDeckId
            )

            if (existingCard != null) {

                val updatedCard = existingCard.copy(
                    question = cardDto.question,
                    answer = cardDto.answer,
                    wrongAnswer1 = cardDto.otherAnswers.items.getOrNull(0),
                    wrongAnswer2 = cardDto.otherAnswers.items.getOrNull(1),
                    wrongAnswer3 = cardDto.otherAnswers.items.getOrNull(2)
                )

                if (existingCard != updatedCard) {
                    Log.d(TAG, "Обновление карточки. Старое: $existingCard, новое: $updatedCard")
                    database.cardDao.updateCard(updatedCard)
                }

                val newAttachment = cardDto.attachment
                val localAttachment = existingCard.attachment ?: ""

                if (newAttachment.isNotBlank() && newAttachment != localAttachment) {
                    Log.d(TAG, "Attachment изменился, скачиваем изображение: $newAttachment")

                    authRequestWrapper.executeWithAuth { token ->
                        val imageResponse = apiService.getCardPicture(
                            deckId = serverDeckId,
                            cardId = cardDto.id,
                            objectName = newAttachment,
                            authHeader = token
                        )

                        if (imageResponse.isSuccessful) {
                            val inputStream = imageResponse.body()?.byteStream()
                            if (inputStream != null) {
                                val file = File(
                                    context.filesDir,
                                    "card_${existingCard.id}_$localDeckId.jpg"
                                )
                                file.outputStream().use { output ->
                                    inputStream.copyTo(output)
                                }

                                database.cardDao.updateCard(
                                    existingCard.copy(
                                        attachment = newAttachment,
                                        picturePath = file.absolutePath
                                    )
                                )
                                Log.d(TAG, "Картинка загружена: ${file.absolutePath}")
                            }
                        } else {
                            Log.e(
                                TAG,
                                "Ошибка при загрузке картинки: ${
                                    imageResponse.errorBody()?.string()
                                }"
                            )
                        }
                    }
                }

            } else {
                Log.d(TAG, "Добавление новой карточки: $cardDto")

                val newCard = CardDBO(
                    id = 0,
                    serverCardId = cardDto.id,
                    deckId = localDeckId,
                    question = cardDto.question,
                    answer = cardDto.answer,
                    wrongAnswer1 = cardDto.otherAnswers.items.getOrNull(0),
                    wrongAnswer2 = cardDto.otherAnswers.items.getOrNull(1),
                    wrongAnswer3 = cardDto.otherAnswers.items.getOrNull(2),
                    attachment = if (cardDto.attachment.isNotBlank()) cardDto.attachment else null,
                    picturePath = null
                )

                val generatedId = database.cardDao.insertCard(newCard)

                var picturePath: String? = null

                if (cardDto.attachment.isNotBlank()) {
                    authRequestWrapper.executeWithAuth { token ->
                        val imageResponse = apiService.getCardPicture(
                            deckId = serverDeckId,
                            cardId = cardDto.id,
                            objectName = cardDto.attachment,
                            authHeader = token
                        )

                        if (imageResponse.isSuccessful) {
                            val inputStream = imageResponse.body()?.byteStream()
                            if (inputStream != null) {
                                val file = File(
                                    context.filesDir,
                                    "card_${generatedId}_$localDeckId.jpg"
                                )
                                file.outputStream().use { output ->
                                    inputStream.copyTo(output)
                                }
                                picturePath = file.absolutePath

                                database.cardDao.updateCard(
                                    newCard.copy(
                                        id = generatedId.toInt(),
                                        picturePath = picturePath
                                    )
                                )
                                Log.d(TAG, "Картинка успешно загружена: $picturePath")
                            }
                        } else {
                            Log.e(
                                TAG,
                                "Ошибка при загрузке картинки: ${
                                    imageResponse.errorBody()?.string()
                                }"
                            )
                        }
                    }
                }
            }
        }

        localCards.forEach { localCard ->
            if (localCard.serverCardId != null && localCard.serverCardId !in serverCardIds) {
                Log.d(TAG, "Удаляем карточку, отсутствующую на сервере: $localCard")

                localCard.picturePath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val deleted = file.delete()
                        Log.d(TAG, "Удаление изображения: $deleted ($path)")
                    }
                }

                database.cardDao.deleteCard(localCard)
            }
        }
    }

    private suspend fun syncTrainingHistoryWithServer(
        userId: String,
        token: String,
    ) {
        val lastSyncTime = getLastSyncTime(userId = userId)

        val response = fetchServerHistory(fromTime = lastSyncTime, token = token)

        if (response.isSuccessful) {
            processServerHistory(response.body()?.items.orEmpty(), userId)
        } else {
            logSyncError(response)
        }

        syncLocalHistoryWithServer(userId, token)
    }

    private suspend fun getLastSyncTime(userId: String): Int {
        return database.historyDao.getLastSyncTime(userId)
            ?.let { (it / MILLIS_IN_SECOND + ONE_SECOND).toInt() }
            ?: 0
    }

    private suspend fun fetchServerHistory(fromTime: Int, token: String) =
        apiService.getUserHistory(fromTime = fromTime, authHeader = token)

    private suspend fun processServerHistory(historyItems: List<HistoryItemDto>, userId: String) {
        if (historyItems.isEmpty()) {
            Log.d(TAG, "Сервер не вернул новые данные истории для синхронизации.")
            return
        }

        Log.d(TAG, "Получено ${historyItems.size} записей истории с сервера.")
        historyItems.forEach { saveHistoryItem(it, userId) }
        Log.d(TAG, "История с сервера успешно синхронизирована.")
    }

    private suspend fun saveHistoryItem(historyItem: HistoryItemDto, userId: String) {
        val deck = database.deckDao.getDeckByServerId(historyItem.collectionId)
        val deckId = deck?.id ?: historyItem.collectionId
        val source = if (deck != null) Source.LOCAL else Source.NETWORK
        val trainingSessionId = generateTrainingSessionId(deckId)

        val history = HistoryDBO(
            id = DEFAULT_HISTORY_ID,
            userId = userId,
            deckId = deckId,
            deckName = historyItem.collectionName,
            cardsCount = historyItem.allCardsCount,
            timestamp = historyItem.time * MILLIS_IN_SECOND,
            trainingSessionId = trainingSessionId,
            source = source,
            isSynchronized = true
        )
        database.historyDao.insertHistory(history)

        historyItem.errors.forEach { saveErrorAnswer(it, trainingSessionId, deckId) }
        historyItem.rightAnswers.forEach { saveCorrectAnswer(it, trainingSessionId, deckId) }
    }

    private suspend fun saveErrorAnswer(
        errorAnswer: ErrorAnswerDto,
        trainingSessionId: String,
        deckId: String,
    ) {
        val cardId =
            database.cardDao.getCardByServerId(errorAnswer.cardId, deckId)?.id ?: errorAnswer.cardId
        val errorAnswerDBO = ErrorAnswerDBO(
            id = DEFAULT_ERROR_ANSWER_ID,
            trainingSessionId = trainingSessionId,
            cardId = cardId,
            question = errorAnswer.question,
            answer = errorAnswer.answer,
            userAnswer = errorAnswer.userAnswer,
            blankAnswer = errorAnswer.blankAnswer,
            trainingMode = mapTrainingMode(errorAnswer.type)
        )
        database.errorDao.insertError(errorAnswerDBO)
    }

    private suspend fun saveCorrectAnswer(
        correctAnswer: CorrectAnswerDto,
        trainingSessionId: String,
        deckId: String,
    ) {
        val cardId = database.cardDao.getCardByServerId(correctAnswer.cardId, deckId)?.id
            ?: correctAnswer.cardId
        val correctAnswerDBO = CorrectAnswerDBO(
            id = DEFAULT_CORRECT_ANSWER_ID,
            cardId = cardId,
            trainingMode = mapTrainingMode(correctAnswer.type),
            trainingSessionId = trainingSessionId
        )
        database.correctAnswerDao.insertCorrectAnswer(correctAnswerDBO)
    }

    private fun logSyncError(response: Response<*>) {
        Log.e(
            TAG,
            "Ошибка при получении истории с сервера. HTTP ${response.code()} - ${
                response.errorBody()?.string()
            }"
        )
    }

    private suspend fun syncLocalHistoryWithServer(userId: String, token: String) {
        val historyToSync = database.historyDao.getHistoryToSync(userId)
        if (historyToSync.isEmpty()) {
            Log.d(TAG, "Нет записей для синхронизации истории.")
            return
        }

        Log.d(TAG, "Начинаем синхронизацию истории. Найдено ${historyToSync.size} записей.")
        historyToSync.forEach { syncSingleHistoryItem(it, token) }
        Log.d(TAG, "Синхронизация истории тренировок завершена.")
    }

    private suspend fun syncSingleHistoryItem(history: HistoryDBO, token: String) {
        val serverDeckId =
            database.deckDao.getDeckById(history.deckId)?.serverDeckId ?: history.deckId
        val historyDto = prepareHistoryDto(
            deckId = serverDeckId,
            deckName = history.deckName,
            cardsCount = history.cardsCount,
            trainingSessionId = history.trainingSessionId,
            timestamp = history.timestamp
        )

        val response = apiService.addTrainingToHistory(historyDto, authHeader = token)
        if (response.isSuccessful) {
            database.historyDao.updateHistory(history.copy(isSynchronized = true))
        } else {
            Log.e(
                TAG,
                "Ошибка синхронизации (Session ID: ${history.trainingSessionId}). HTTP ${response.code()} - ${
                    response.errorBody()?.string()
                }"
            )
        }
    }

    private suspend fun prepareHistoryDto(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        trainingSessionId: String,
        timestamp: Long,
    ): HistoryItemDto {
        val errorAnswers =
            database.errorDao.getErrorAnswersForTrainingSession(trainingSessionId).map {
                ErrorAnswerDto(
                    cardId = it.cardId,
                    question = it.question,
                    answer = it.answer,
                    type = it.trainingMode.name,
                    userAnswer = it.userAnswer,
                    blankAnswer = it.blankAnswer
                )
            }

        val correctAnswers =
            database.correctAnswerDao.getCorrectAnswersForTrainingSession(trainingSessionId).map {
                CorrectAnswerDto(
                    cardId = it.cardId,
                    type = it.trainingMode.name
                )
            }

        return HistoryItemDto(
            collectionId = deckId,
            collectionName = deckName,
            time = (timestamp / MILLIS_IN_SECOND).toInt(),
            correctCards = correctAnswers.map { it.cardId },
            incorrectCards = errorAnswers.map { it.cardId },
            allCardsCount = cardsCount,
            errors = errorAnswers,
            rightAnswers = correctAnswers
        )
    }

    private fun generateTrainingSessionId(deckId: String): String {
        return "$deckId-${System.currentTimeMillis()}"
    }

    private fun mapTrainingMode(mode: String): TrainingMode {
        return when (mode) {
            "FILL_IN_THE_BLANK" -> TrainingMode.FILL_IN_THE_BLANK
            "MULTIPLE_CHOICE" -> TrainingMode.MULTIPLE_CHOICE
            "TRUE_FALSE" -> TrainingMode.TRUE_FALSE
            else -> throw IllegalArgumentException("Неизвестный режим тренировки: $mode")
        }
    }

    companion object {

        private const val TAG = "SyncWorker"
        private const val MILLIS_IN_SECOND = 1000L
        private const val ONE_SECOND = 1L
        private const val DEFAULT_HISTORY_ID = 0L
        private const val DEFAULT_CORRECT_ANSWER_ID = 0L
        private const val DEFAULT_ERROR_ANSWER_ID = 0L
    }
}