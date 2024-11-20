package com.example.decks.domain.usecase

import com.example.decks.domain.repository.DeckRepository
import javax.inject.Inject

/**
 * Use case for loading the next set of public decks.
 *
 * This class encapsulates the logic for loading additional public decks from the repository.
 *
 * @param repository The repository that provides deck data.
 */
internal class LoadNextPublicDecksUseCase @Inject constructor(
    private val repository: DeckRepository
) {

    suspend operator fun invoke() {
        return repository.loadNextPublicDecks()
    }
}