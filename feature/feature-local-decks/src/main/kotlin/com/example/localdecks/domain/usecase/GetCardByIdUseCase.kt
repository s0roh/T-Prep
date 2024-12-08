package com.example.localdecks.domain.usecase

import com.example.common.domain.entity.Card
import com.example.localdecks.domain.repository.LocalDeckRepository
import javax.inject.Inject

class GetCardByIdUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    suspend operator fun invoke(cardId: Long): Card? = repository.getCardById(cardId)
}