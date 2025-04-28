package com.example.training.domain.repository

import android.net.Uri
import com.example.common.domain.entity.Card
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.training.domain.entity.TrainingCard
import com.example.training.domain.entity.TrainingError
import com.example.training.domain.entity.TrainingModes

interface TrainingRepository {

    /**
     * Подготавливает карточки для тренировки с учетом выбранных режимов.
     *
     * @param deckId Идентификатор колоды.
     * @param cards Список карточек.
     * @param modes Набор выбранных режимов тренировки.
     * @return Список подготовленных карточек для тренировки [TrainingCard].
     */
    suspend fun prepareTrainingCards(
        deckId: String,
        cards: List<Card>,
        modes: Set<TrainingMode>,
    ): List<TrainingCard>

    /**
     * Записывает информацию о тренировке.
     *
     * @param deckId Идентификатор колоды.
     * @param deckName Название колоды.
     * @param cardsCount Количество карточек в тренировке.
     * @param source Источник колоды.
     * @param trainingSessionId Идентификатор сессии тренировки.
     */
    suspend fun recordTraining(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        source: Source,
        trainingSessionId: String,
    )

    /**
     * Записывает ответ на карточку.
     *
     * @param cardId Идентификатор карточки.
     * @param question Вопрос карточки.
     * @param correctAnswer Правильный ответ.
     * @param fillInTheBlankAnswer Ответ при частичном отображении.
     * @param incorrectAnswer Неправильный ответ.
     * @param isCorrect Указывает, был ли ответ правильным.
     * @param trainingSessionId Идентификатор сессии тренировки.
     * @param trainingMode Режим тренировки.
     * @param attachment Приложение (опционально).
     */
    suspend fun recordAnswer(
        cardId: Int,
        question: String,
        correctAnswer: String,
        fillInTheBlankAnswer: String? = null,
        incorrectAnswer: String? = null,
        isCorrect: Boolean,
        trainingSessionId: String,
        trainingMode: TrainingMode,
        attachment: String? = null,
    )

    /**
     * Получает изображение карточки.
     *
     * @param deckId Идентификатор колоды.
     * @param cardId Идентификатор карточки.
     * @param source Источник колоды.
     * @param attachment Приложение (опционально).
     * @return URI изображения карточки.
     */
    suspend fun getCardPicture(
        deckId: String,
        cardId: Int,
        source: Source,
        attachment: String? = null,
    ): Uri?

    /**
     * Сохраняет режимы тренировки.
     *
     * @param trainingModes Режимы тренировки.
     */
    suspend fun saveTrainingModes(trainingModes: TrainingModes)

    /**
     * Получает режимы тренировки для колоды.
     *
     * @param deckId Идентификатор колоды.
     * @return Режимы тренировки.
     */
    suspend fun getTrainingModes(deckId: String): TrainingModes

    /**
     * Проверяет правильность ответа в режиме "Заполни пропуск".
     *
     * @param userInput Введённый пользователем ответ.
     * @param correctWords Список правильных слов.
     * @return Результат проверки.
     */
    suspend fun checkFillInTheBlankAnswer(userInput: String, correctWords: List<String>): Boolean

    /**
     * Получает количество правильных и общее количество ответов для сессии тренировки.
     *
     * @param trainingSessionId Идентификатор сессии тренировки.
     * @return Пара значений: общее количество ответов и количество правильных.
     */
    suspend fun getTotalAndCorrectCountAnswers(trainingSessionId: String): Pair<Int, Int>

    /**
     * Получает время следующей тренировки для сессии.
     *
     * @param trainingSessionId Идентификатор сессии тренировки.
     * @return Время следующей тренировки в миллисекундах.
     */
    suspend fun getNextTrainingTime(trainingSessionId: String): Long?

    /**
     * Получает список ошибок для сессии тренировки.
     *
     * @param trainingSessionId Идентификатор сессии тренировки.
     * @return Список ошибок.
     */
    suspend fun getErrorsList(trainingSessionId: String): List<TrainingError>

    /**
     * Получает имя колоды и время тренировки для сессии.
     *
     * @param trainingSessionId Идентификатор сессии тренировки.
     * @return Тройка: имя колоды, время тренировки и источник.
     */
    suspend fun getDeckNameAndTrainingSessionTime(trainingSessionId: String): Triple<String, Long, Source>

    /**
     * Получает информацию для навигации к колоде по сессии тренировки.
     *
     * @param trainingSessionId Идентификатор сессии тренировки.
     * @return Пара: имя колоды и источник.
     */
    suspend fun getInfoForNavigationToDeck(trainingSessionId: String): Pair<String, Source>
}