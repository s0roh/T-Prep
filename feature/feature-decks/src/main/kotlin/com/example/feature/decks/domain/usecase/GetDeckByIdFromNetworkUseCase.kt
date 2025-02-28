package com.example.feature.decks.domain.usecase

import com.example.common.domain.entity.Deck
import com.example.database.models.Source
import com.example.decks.domain.repository.PublicDeckRepository
import javax.inject.Inject

/**
 * Use case for retrieving a deck by its ID.
 *
 * This class encapsulates the logic for fetching a deck from the repository using its unique identifier.
 *
 * @param repository The repository that provides deck data.
 */
internal class GetDeckByIdFromNetworkUseCase @Inject constructor(
    private val repository: PublicDeckRepository
) {

    suspend operator fun invoke(id: String): Pair<Deck, Source> {
        return repository.getDeckById(id)
    }
}