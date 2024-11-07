package com.example.t_prep.domain.usecase

import com.example.t_prep.domain.repository.PrepRepository

class LoadNextPublicDecksUseCase(
    private val repository: PrepRepository
) {

    suspend operator fun invoke() {
        return repository.loadNextPublicDecks()
    }
}