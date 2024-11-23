package com.example.training.data

import com.example.common.domain.entity.Card
import com.example.database.TPrepDatabase
import com.example.database.models.HistoryDBO
import com.example.database.models.Source
import com.example.training.domain.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    private val database: TPrepDatabase
) : TrainingRepository {

    override suspend fun prepareTrainingCards(
        deckId: Long,
        cards: List<Card>,
        source: Source
    ): List<Card> {
        return withContext(Dispatchers.IO) {
            val cardsWithSortingData = cards.map { card ->
                val isNew = database.historyDao.isCardNew(card.id, deckId, source)
                val coefficient = calculateCoefficient(deckId, card.id, source)
                card to Pair(isNew, coefficient)
            }

            // Сортируем по предварительно вычисленным данным
            cardsWithSortingData
                .sortedWith(compareBy(
                    { if (it.second.first) 0 else 1 }, // Сначала новые карточки
                    { it.second.second }              // Затем по коэффициенту
                ))
                .map { it.first } // Возвращаем только сами карточки
        }
    }

    override suspend fun recordAnswer(
        cardId: Long,
        deckId: Long,
        isCorrect: Boolean,
        source: Source
    ) {
        val historyEntry = HistoryDBO(
            id = 0,
            cardId = cardId,
            deckId = deckId,
            timestamp = System.currentTimeMillis(),
            isCorrect = isCorrect,
            source = source
        )
        database.historyDao.insertHistory(historyEntry)
    }

    private suspend fun calculateCoefficient(deckId: Long, cardId: Long, source: Source): Double {
        val correctCount =
            database.historyDao.getCorrectAnswersCountForCard(cardId, deckId, source)
        val totalCount = database.historyDao.getTotalAnswersCount(cardId, deckId, source)

        if (totalCount == 0) return 2.5

        val accuracy = correctCount.toDouble() / totalCount
        return (1.3 + accuracy * 1.2).coerceIn(1.3, 2.5)
    }
}