package com.example.training.domain

import com.example.database.models.Source
import javax.inject.Inject

internal class RecordAnswerUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(
        deckId: Long,
        deckName: String,
        cardsCount: Int,
        cardId: Long,
        isCorrect: Boolean,
        source: Source
    ) =
        repository.recordAnswer(
            deckId = deckId,
            deckName = deckName,
            cardsCount = cardsCount,
            cardId = cardId,
            isCorrect = isCorrect,
            source = source
        )
}