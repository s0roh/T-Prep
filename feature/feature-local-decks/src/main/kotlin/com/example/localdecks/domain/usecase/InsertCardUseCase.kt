package com.example.localdecks.domain.usecase

import com.example.common.domain.entity.Card
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class InsertCardUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(card: Card, deckId: String) =
        repository.insertCard(card = card, deckId = deckId)
}