package com.example.training.domain

import com.example.database.models.Source
import javax.inject.Inject

class RecordAnswerUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(deckId: Long, cardId: Long, isCorrect: Boolean, source: Source) =
        repository.recordAnswer(
            deckId = deckId,
            cardId = cardId,
            isCorrect = isCorrect,
            source = source
        )
}