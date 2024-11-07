package com.example.t_prep.domain.repository

import com.example.t_prep.domain.entity.Deck
import kotlinx.coroutines.flow.StateFlow

interface PrepRepository {

    fun getPublicDecksFlow(): StateFlow<List<Deck>>

    suspend fun loadNextPublicDecks()
}