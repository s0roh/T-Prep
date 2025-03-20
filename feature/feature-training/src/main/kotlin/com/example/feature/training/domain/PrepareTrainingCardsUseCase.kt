package com.example.feature.training.domain

import com.example.common.domain.entity.Card
import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.training.domain.entity.TrainingCard
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

internal class PrepareTrainingCardsUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(
        deckId: String,
        cards: List<Card>,
        modes: Set<TrainingMode>,
    ): List<TrainingCard> =
        repository.prepareTrainingCards(
            deckId = deckId,
            cards = cards,
            modes = modes
        )
}