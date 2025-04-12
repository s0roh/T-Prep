package com.example.training.domain.repository

import android.net.Uri
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
        modes: Set<TrainingMode>,
    ): List<TrainingCard>

    suspend fun recordTraining(
        deckId: String,
        deckName: String,
        cardsCount: Int,
        source: Source,
        trainingSessionId: String,
    )

    suspend fun recordAnswer(
        cardId: Int,
        question: String,
        correctAnswer: String,
        fillInTheBlankAnswer: String? = null,
        incorrectAnswer: String? = null,
        isCorrect: Boolean,
        trainingSessionId: String,
        trainingMode: TrainingMode,
    )

    suspend fun getCardPicture(
        deckId: String,
        cardId: Int,
        source: Source,
        attachment: String? = null,
    ): Uri?

    suspend fun saveTrainingModes(trainingModes: TrainingModes)

    suspend fun getTrainingModes(deckId: String): TrainingModes

    suspend fun checkFillInTheBlankAnswer(userInput: String, correctWords: List<String>): Boolean

    suspend fun getTotalAndCorrectCountAnswers(trainingSessionId: String): Pair<Int, Int>

    suspend fun getNextTrainingTime(trainingSessionId: String): Long?

    suspend fun getErrorsList(trainingSessionId: String): List<TrainingError>

    suspend fun getDeckNameAndTrainingSessionTime(trainingSessionId: String): Pair<String, Long>

    suspend fun getInfoForNavigationToDeck(trainingSessionId: String): Pair<String, Source>
}