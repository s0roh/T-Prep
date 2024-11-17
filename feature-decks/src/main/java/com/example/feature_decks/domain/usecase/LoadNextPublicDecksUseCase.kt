package com.example.feature_decks.domain.usecase

import com.example.data_decks.domain.repository.DeckRepository
import javax.inject.Inject

internal class LoadNextPublicDecksUseCase @Inject constructor(
    private val repository: DeckRepository
) {

    suspend operator fun invoke() {
        return repository.loadNextPublicDecks()
    }
}