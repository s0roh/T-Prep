package com.example.t_prep.data.repository

import com.example.t_prep.data.mapper.toEntity
import com.example.t_prep.data.network.api.ApiFactory
import com.example.t_prep.domain.entity.Deck
import com.example.t_prep.domain.repository.PrepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class PrepRepositoryImpl : PrepRepository {

    private val apiService = ApiFactory.apiService
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _decks = mutableListOf<Deck>()
    private val decks: List<Deck>
        get() = _decks.toList()

    private val nextDataNeededEvents = MutableSharedFlow<Unit>(replay = 1)
    private var nextFrom: Int = 0

    private val loadedListFlow = flow {
        nextDataNeededEvents.emit(Unit)
        nextDataNeededEvents.collect {
            val response = apiService.getPublicDecks(nextFrom = nextFrom)
            if (response.count == 0) return@collect
            nextFrom += response.count
            val currentDecks = response.decks.map { it.toEntity() }
            _decks.addAll(currentDecks)
            emit(decks)
        }
    }

    override fun getPublicDecksFlow(): StateFlow<List<Deck>> = loadedListFlow.stateIn(
        coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = decks
    )

    override suspend fun loadNextPublicDecks() {
        nextDataNeededEvents.emit(Unit)
    }
}