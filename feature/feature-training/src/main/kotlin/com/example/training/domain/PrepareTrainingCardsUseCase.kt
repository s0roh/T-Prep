package com.example.training.domain

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.TrainingMode
import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

internal class PrepareTrainingCardsUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(
        deckId: String,
        cards: List<Card>,
        source: Source,
        modes: Set<TrainingMode> = setOf<TrainingMode>(
            TrainingMode.MULTIPLE_CHOICE,
            TrainingMode.TRUE_FALSE
        )
    ): List<Card> =
        repository.prepareTrainingCards(
            deckId = deckId,
            cards = cards,
            source = source,
            modes = modes
        )
}