package com.example.data_decks.data.repository

import com.example.core_network.api.ApiFactory
import com.example.data_decks.data.mapper.toEntity
import com.example.data_decks.domain.entity.Deck
import com.example.data_decks.domain.repository.DeckRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn

class DeckRepositoryImpl : DeckRepository {

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
            if (response.decks.isEmpty()) return@collect
            nextFrom += response.decks.size
            val currentDecks = response.decks.map { it.toEntity() }
            _decks.addAll(currentDecks)
            emit(decks)
        }
    }.retry {
        delay(RETRY_TIMEOUT_MILLIS)
        true
    }.stateIn(
        coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = decks
    )

    override fun getPublicDecksFlow(): StateFlow<List<Deck>> = loadedListFlow

    override suspend fun loadNextPublicDecks() {
        nextDataNeededEvents.emit(Unit)
    }

    companion object {

        private const val RETRY_TIMEOUT_MILLIS = 3000L
    }
}