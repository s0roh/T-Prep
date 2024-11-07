package com.example.t_prep.domain.usecase

import com.example.t_prep.domain.entity.Deck
import com.example.t_prep.domain.repository.PrepRepository
import kotlinx.coroutines.flow.StateFlow

class GetPublicDecksFlowUseCase(
    private val repository: PrepRepository
) {

    operator fun invoke(): StateFlow<List<Deck>> {
        return repository.getPublicDecksFlow()
    }
}