package com.example.decks.domain.usecase

import com.example.decks.domain.entity.Deck
import com.example.decks.domain.repository.DeckRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

/**
 * Use case for retrieving a flow of public decks.
 *
 * This class encapsulates the logic for obtaining a stream of public decks from the repository.
 *
 * @param repository The repository that provides deck data.
 */
internal class GetPublicDecksFlowUseCase @Inject constructor(
    private val repository: DeckRepository
) {

    operator fun invoke(): SharedFlow<List<Deck>> {
        return repository.getPublicDecksFlow()
    }
}