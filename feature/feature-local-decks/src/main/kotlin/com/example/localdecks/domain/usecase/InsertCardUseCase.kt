package com.example.localdecks.domain.usecase

import com.example.common.domain.entity.Card
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

class InsertCardUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(card: Card, deckId: Long) =
        repository.insertCard(card = card, deckId = deckId)
}