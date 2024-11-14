package com.example.feature_decks.domain.usecase

import com.example.data_decks.domain.repository.DeckRepository

internal class LoadNextPublicDecksUseCase(
    private val repository: DeckRepository
) {

    suspend operator fun invoke() {
        return repository.loadNextPublicDecks()
    }
}