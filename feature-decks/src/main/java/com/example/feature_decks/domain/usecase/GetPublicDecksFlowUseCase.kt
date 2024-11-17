package com.example.feature_decks.domain.usecase

import com.example.data_decks.domain.entity.Deck
import com.example.data_decks.domain.repository.DeckRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

internal class GetPublicDecksFlowUseCase @Inject constructor(
    private val repository: DeckRepository
) {

    operator fun invoke(): SharedFlow<List<Deck>> {
        return repository.getPublicDecksFlow()
    }
}