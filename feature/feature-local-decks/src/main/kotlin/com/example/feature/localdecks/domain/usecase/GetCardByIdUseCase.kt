package com.example.feature.localdecks.domain.usecase

import com.example.common.domain.entity.Card
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

internal class GetCardByIdUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(cardId: Int): Card? = repository.getCardById(cardId)
}