package com.example.training.domain

import com.example.common.domain.entity.Deck
import com.example.decks.domain.repository.PublicDeckRepository
import javax.inject.Inject

/**
 * Use case for retrieving a deck by its ID.
 *
 * This class encapsulates the logic for fetching a deck from the repository using its unique identifier.
 *
 * @param repository The repository that provides deck data.
 */
internal class GetDeckByIdUseCase @Inject constructor(
    private val repository: PublicDeckRepository
) {

    suspend operator fun invoke(id: Long): Deck {
        return repository.getDeckById(id)
    }
}