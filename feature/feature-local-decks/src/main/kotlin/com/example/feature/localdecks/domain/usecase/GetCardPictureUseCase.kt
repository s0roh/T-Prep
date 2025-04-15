package com.example.feature.localdecks.domain.usecase

import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class GetCardPictureUseCase @Inject constructor(
    private val repository: LocalDeckRepository,
) {

    suspend operator fun invoke(deckId: String, cardId: Int) =
        repository.getCardPicture(deckId = deckId, cardId = cardId)
}