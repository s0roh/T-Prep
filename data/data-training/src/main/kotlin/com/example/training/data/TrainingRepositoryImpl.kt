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
        deckId: String,
        cards: List<Card>,
        source: Source
    ): List<Card> {
        return withContext(Dispatchers.IO) {
            val cardsWithSortingData = cards.map { card ->
                val history = database.historyDao.getHistoryForCard(
                    cardId = card.id,
                    deckId = deckId,
                    source = source
                )
                val isNew = history == null
                val coefficient = history?.coefficient ?: DEFAULT_COEFFICIENT
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
        source: Source
    ) {
        val history = database.historyDao.getHistoryForCard(
            cardId = cardId,
            deckId = deckId,
            source = source
        )

        val newCoefficient = calculateUpdatedCoefficient(
            currentCoefficient = history?.coefficient ?: DEFAULT_COEFFICIENT,
            isCorrect = isCorrect
        )

        val updatedHistory = HistoryDBO(
            id = history?.id ?: 0,
            deckId = deckId,
            deckName = deckName,
            cardsCount = cardsCount,
            cardId = cardId,
            timestamp = System.currentTimeMillis(),
            isCorrect = isCorrect,
            source = source,
            coefficient = newCoefficient
        )

        database.historyDao.insertOrUpdateHistory(updatedHistory)
    }

    private fun calculateUpdatedCoefficient(
        currentCoefficient: Double,
        isCorrect: Boolean
    ): Double {
        val adjustment = if (isCorrect) COEFFICIENT_INCREMENT else COEFFICIENT_DECREMENT
        return (currentCoefficient + adjustment).coerceIn(MIN_COEFFICIENT, MAX_COEFFICIENT)
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