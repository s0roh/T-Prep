package com.example.feature.localdecks.domain.usecase

import android.net.Uri
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class UpdateCardPictureUseCase @Inject constructor(
    private val repository: LocalDeckRepository,
) {

    suspend operator fun invoke(deckId: String, cardId: Int, pictureUri: Uri) =
        repository.updateCardPicture(deckId = deckId, cardId = cardId, pictureUri = pictureUri)
}