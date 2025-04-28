package com.example.history.domain.repository

import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.entity.TrainingModeStats

interface HistoryRepository {

    /**
     * Получает историю всех тренировок.
     *
     * @return Список объектов [TrainingHistoryItem], представляющих историю всех тренировок.
     */
    suspend fun getTrainingHistory(): List<TrainingHistoryItem>

    /**
     * Получает статистику по всем тренировкам.
     *
     * @return Пара значений: общее количество тренировок и средний процент правильных ответов.
     */
    suspend fun getTrainingStats(): Pair<Int, Int>

    /**
     * Получает статистику по тренировкам для конкретной колоды.
     *
     * @param deckId Идентификатор колоды, для которой нужно получить статистику.
     * @return Список значений от 0 до 100, представляющих успешность прохождения тренировки для данной колоды.
     */
    suspend fun getDeckTrainingStats(deckId: String): List<Double>

    /**
     * Получает статистику по режимам тренировки для конкретной колоды.
     *
     * @param deckId Идентификатор колоды, для которой нужно получить статистику по режимам тренировки.
     * @return Список объектов [TrainingModeStats], содержащих статистику по каждому режиму тренировки.
     */
    suspend fun getDeckTrainingModeStats(deckId: String): List<TrainingModeStats>
}