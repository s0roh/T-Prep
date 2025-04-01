package com.example.feature.profile.domain

import com.example.decks.domain.repository.PublicDeckRepository
import javax.inject.Inject

internal class LikeOrUnlikeUseCase @Inject constructor(
    private val repository: PublicDeckRepository,
) {

    suspend operator fun invoke(deckId: String, isLiked: Boolean): Int =
        repository.likeOrUnlikeDeck(deckId = deckId, isLiked = isLiked)
}