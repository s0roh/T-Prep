package com.example.feature.decks.domain.usecase

import android.net.Uri
import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import javax.inject.Inject

internal class GetCardPictureUseCase @Inject constructor(
    private val repository: TrainingRepository,
) {

    suspend operator fun invoke(deckId: String, cardId: Int, source: Source, attachment: String?): Uri? =
        repository.getCardPicture(
            deckId = deckId,
            cardId = cardId,
            source = source,
            attachment = attachment
        )
}