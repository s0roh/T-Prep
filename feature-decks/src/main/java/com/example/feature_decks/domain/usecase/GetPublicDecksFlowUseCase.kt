package com.example.feature_decks.domain.usecase

import com.example.data_decks.domain.entity.Deck
import com.example.data_decks.domain.repository.DeckRepository
import kotlinx.coroutines.flow.StateFlow

internal class GetPublicDecksFlowUseCase(
    private val repository: DeckRepository
) {

    operator fun invoke(): StateFlow<List<Deck>> {
        return repository.getPublicDecksFlow()
    }
}