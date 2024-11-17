package com.example.feature_decks.domain.usecase

import com.example.data_decks.domain.entity.Deck
import com.example.data_decks.domain.repository.DeckRepository
import javax.inject.Inject

class GetDeckByIdUseCase @Inject constructor(
    private val repository: DeckRepository
) {

    suspend operator fun invoke(id: Long): Deck {
        return repository.getDeckById(id)
    }
}