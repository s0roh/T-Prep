package com.example.training.domain

import com.example.common.domain.entity.Card
import com.example.database.models.Source
import javax.inject.Inject

class PrepareTrainingCardsUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke(deckId: Long, cards: List<Card>, source: Source): List<Card> =
        repository.prepareTrainingCards(
            deckId = deckId,
            cards = cards,
            source = source
        )
}