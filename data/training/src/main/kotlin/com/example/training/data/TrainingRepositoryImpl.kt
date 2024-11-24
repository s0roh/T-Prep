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
                val isNew = database.historyDao.isCardNew(
                    cardId = card.id,
                    deckId = deckId,
                    source = source
                )
                val coefficient = calculateCoefficient(
                    deckId = deckId,
                    cardId = card.id,
                    source = source
                )
                card to Pair(isNew, coefficient)
            }

            // Сортируем по предварительно вычисленным данным
            val sortedCards = cardsWithSortingData
                .sortedWith(compareBy(
                    { if (it.second.first) 0 else 1 }, // Сначала новые карточки
                    { it.second.second }              // Затем по коэффициенту
                ))
                .map { it.first } // Возвращаем только сами карточки

            sortedCards.map { card ->
                val wrongAnswers = generateWrongAnswers(card, sortedCards)
                card.copy(wrongAnswers = wrongAnswers)
            }
        }
    }

    private fun generateWrongAnswers(
        currentCard: Card,
        allCards: List<Card>
    ): List<String> {
        // Исключаем текущую карточку и фильтруем уникальные ответы
        val otherAnswers = allCards
            .filter { it.id != currentCard.id }
            .map { it.answer }
            .distinct()

        // Перемешиваем и берем максимум 3 неправильных ответа
        return otherAnswers.shuffled().take(3)
    }

    override suspend fun recordAnswer(
        deckId: Long,
        cardId: Long,
        isCorrect: Boolean,
        source: Source
    ) {
        val historyEntry = HistoryDBO(
            id = 0,
            deckId = deckId,
            cardId = cardId,
            timestamp = System.currentTimeMillis(),
            isCorrect = isCorrect,
            source = source
        )
        database.historyDao.insertHistory(historyEntry)
    }

    private suspend fun calculateCoefficient(deckId: Long, cardId: Long, source: Source): Double {
        val correctCount =
            database.historyDao.getCorrectAnswersCountForCard(
                cardId = cardId,
                deckId = deckId,
                source = source
            )
        val totalCount = database.historyDao.getTotalAnswersCount(
            cardId = cardId,
            deckId = deckId,
            source = source
        )

        if (totalCount == 0) return 2.5

        val accuracy = correctCount.toDouble() / totalCount
        return (1.3 + accuracy * 1.2).coerceIn(1.3, 2.5)
    }
}