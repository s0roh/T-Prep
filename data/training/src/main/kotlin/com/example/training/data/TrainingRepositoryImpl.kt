package com.example.training.data

import com.example.common.domain.entity.Card
import com.example.database.TPrepDatabase
import com.example.database.models.HistoryDBO
import com.example.database.models.Source
import com.example.training.domain.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrainingRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase
) : TrainingRepository {

    override suspend fun prepareTrainingCards(
        deckId: Long,
        cards: List<Card>,
        source: Source
    ): List<Card> {
        return withContext(Dispatchers.IO) {
            val cardsWithSortingData = cards.map { card ->
                val isNew = database.historyDao.getTotalAnswersCount(
                    cardId = card.id,
                    deckId = deckId,
                    source = source
                ) == 0
                val coefficient = calculateCoefficient(
                    deckId = deckId,
                    cardId = card.id,
                    source = source
                )
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
        allCards: List<Card>
    ): List<String> {
        val otherAnswers = allCards
            .filter { it.id != currentCard.id }
            .map { it.answer }
            .distinct()

        return otherAnswers.shuffled().take(WRONG_ANSWERS_COUNT)
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

        if (totalCount == 0) return DEFAULT_COEFFICIENT

        val accuracy = correctCount.toDouble() / totalCount
        return (MIN_COEFFICIENT + accuracy * COEFFICIENT_MULTIPLIER).coerceIn(
            MIN_COEFFICIENT,
            MAX_COEFFICIENT
        )
    }

    companion object {

        const val NEW_CARD_PRIORITY = 0
        const val EXISTING_CARD_PRIORITY = 1
        const val DEFAULT_COEFFICIENT = 2.5
        const val MIN_COEFFICIENT = 1.3
        const val MAX_COEFFICIENT = 2.5
        const val COEFFICIENT_MULTIPLIER = 1.2
        const val WRONG_ANSWERS_COUNT = 3
    }
}