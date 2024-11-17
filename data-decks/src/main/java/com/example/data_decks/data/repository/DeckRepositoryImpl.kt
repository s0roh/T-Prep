package com.example.data_decks.data.repository

import com.example.core_network.api.ApiService
import com.example.data_decks.data.mapper.toEntity
import com.example.data_decks.domain.entity.Deck
import com.example.data_decks.domain.repository.DeckRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class DeckRepositoryImpl @Inject internal constructor(
    private val apiService: ApiService
) : DeckRepository {

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
            val responseDecks = response.decks
            if (responseDecks.isNullOrEmpty()) {
                emit(decks)
                return@collect
            }
            nextFrom += responseDecks.size
            val currentDecks = responseDecks.map { it.toEntity() }
            _decks.addAll(currentDecks)
            emit(decks)
        }
    }.retry {
        delay(RETRY_TIMEOUT_MILLIS)
        true
    }

    override fun getPublicDecksFlow(): SharedFlow<List<Deck>> = loadedListFlow.shareIn(
        coroutineScope,
        started = SharingStarted.Lazily,
        replay = 1
    )

    override suspend fun getDeckById(id: Long): Deck {
        return apiService.getDeckById(id).toEntity()
    }

    override suspend fun loadNextPublicDecks() {
        nextDataNeededEvents.emit(Unit)
    }

    companion object {

        private const val RETRY_TIMEOUT_MILLIS = 3000L
    }
}