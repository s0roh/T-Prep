package com.example.decks.domain.usecase

import com.example.common.domain.entity.Card
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(card: Card) = repository.deleteCard(card)
}