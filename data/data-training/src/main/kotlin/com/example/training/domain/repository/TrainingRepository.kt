package com.example.training.domain.repository

import com.example.common.domain.entity.Card
import com.example.database.models.Source
import com.example.training.domain.entity.TrainingError

interface TrainingRepository {

    suspend fun prepareTrainingCards(deckId: String, cards: List<Card>, source: Source): List<Card>

    suspend fun recordAnswer(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        cardId: Int,
        isCorrect: Boolean,
        incorrectAnswer: String? = null,
        source: Source,
        trainingSessionId: String,
    )

    suspend fun getTotalAndCorrectCountAnswers(trainingSessionId: String): Pair<Int, Int>

    suspend fun getNextTrainingTime(trainingSessionId: String): Long?

    suspend fun getErrorsList(trainingSessionId: String): List<TrainingError>

    suspend fun getDeckNameAndTrainingSessionTime(trainingSessionId: String): Pair<String, Long>

    suspend fun getInfoForNavigationToDeck(trainingSessionId: String): Pair<String, Source>
}