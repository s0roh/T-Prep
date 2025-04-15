package com.example.feature.training.domain

import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

internal class GetCardPictureUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(
        deckId: String,
        cardId: Int,
        source: Source,
        attachment: String?,
    ) =
        repository.getCardPicture(
            deckId = deckId,
            cardId = cardId,
            source = source,
            attachment = attachment
        )
}