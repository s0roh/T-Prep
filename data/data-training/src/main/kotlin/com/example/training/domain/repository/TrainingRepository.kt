package com.example.training.domain.repository

import com.example.common.domain.entity.Card
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.training.domain.entity.TrainingCard
import com.example.training.domain.entity.TrainingError
import com.example.training.domain.entity.TrainingModes

interface TrainingRepository {

    suspend fun prepareTrainingCards(
        deckId: String,
        cards: List<Card>,
        source: Source,
        modes: Set<TrainingMode>
    ): List<TrainingCard>

    suspend fun recordAnswer(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        cardId: Int,
        isCorrect: Boolean,
        incorrectAnswer: String? = null,
        source: Source,
        trainingSessionId: String,
        trainingMode: TrainingMode
    )

    suspend fun saveTrainingModes(trainingModes: TrainingModes)

    suspend fun getTrainingModes(deckId: String): TrainingModes

    suspend fun checkFillInTheBlankAnswer(userInput: String, correctWords: List<String>): Boolean

    suspend fun getTotalAndCorrectCountAnswers(trainingSessionId: String): Pair<Int, Int>

    suspend fun getNextTrainingTime(trainingSessionId: String): Long?

    suspend fun getErrorsList(trainingSessionId: String): List<TrainingError>

    suspend fun getDeckNameAndTrainingSessionTime(trainingSessionId: String): Pair<String, Long>

    suspend fun getInfoForNavigationToDeck(trainingSessionId: String): Pair<String, Source>
}