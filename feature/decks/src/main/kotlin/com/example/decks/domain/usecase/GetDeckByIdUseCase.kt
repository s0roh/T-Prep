package com.example.decks.domain.usecase

import com.example.decks.domain.entity.Deck
import com.example.decks.domain.repository.DeckRepository
import javax.inject.Inject

/**
 * Use case for retrieving a deck by its ID.
 *
 * This class encapsulates the logic for fetching a deck from the repository using its unique identifier.
 *
 * @param repository The repository that provides deck data.
 */
class GetDeckByIdUseCase @Inject constructor(
    private val repository: DeckRepository
) {

    suspend operator fun invoke(id: Long): Deck {
        return repository.getDeckById(id)
    }
}