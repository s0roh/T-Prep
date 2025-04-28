package com.example.decks.domain.repository

interface DeckDetailsRepository {

    /**
     * Получает время следующей тренировки для колоды.
     *
     * @param deckId Идентификатор колоды.
     * @return Время следующей тренировки в миллисекундах, если оно существует; null — если тренировка не назначена.
     */
    suspend fun getNextTrainingTime(deckId: String): Long?
}