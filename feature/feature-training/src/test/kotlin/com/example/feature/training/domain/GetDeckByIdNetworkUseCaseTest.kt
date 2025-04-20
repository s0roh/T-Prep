package com.example.feature.training.domain

import com.example.common.domain.entity.Deck
import com.example.database.models.Source
import com.example.decks.domain.repository.PublicDeckRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDeckByIdNetworkUseCaseTest {

    private val repository: PublicDeckRepository = mockk(relaxed = true)
    private lateinit var getDeckByIdNetworkUseCase: GetDeckByIdNetworkUseCase

    @BeforeEach
    fun setup() {
        getDeckByIdNetworkUseCase = GetDeckByIdNetworkUseCase(repository)
    }

    @Test
    fun `should return deck from local source when deck is found in local database`() = runTest {
        val deckId = "deck1"
        val expectedDeck = Deck(
            id = deckId,
            name = "Deck 1",
            isPublic = true,
            authorId = "author1",
            cards = emptyList()
        )
        val localDeckPair = expectedDeck to Source.LOCAL

        coEvery { repository.getDeckById(deckId) } returns localDeckPair

        val result = getDeckByIdNetworkUseCase(deckId)

        assertThat(result).isEqualTo(localDeckPair)
    }

    @Test
    fun `should return deck from network source when deck is not found in local database`() = runTest {
        val deckId = "deck2"
        val expectedDeck = Deck(
            id = deckId,
            name = "Deck 2",
            isPublic = true,
            authorId = "author2",
            cards = emptyList()
        )
        val networkDeckPair = expectedDeck to Source.NETWORK

        coEvery { repository.getDeckById(deckId) } returns networkDeckPair

        val result = getDeckByIdNetworkUseCase(deckId)

        assertThat(result).isEqualTo(networkDeckPair)
    }
}