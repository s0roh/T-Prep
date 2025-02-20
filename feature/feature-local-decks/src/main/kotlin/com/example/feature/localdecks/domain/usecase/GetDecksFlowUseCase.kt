package com.example.feature.localdecks.domain.usecase

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetDecksFlowUseCase @Inject constructor(
    private val repository: LocalDeckRepository
) {

    operator fun invoke(): Flow<List<Deck>> = repository.getDecks()
}