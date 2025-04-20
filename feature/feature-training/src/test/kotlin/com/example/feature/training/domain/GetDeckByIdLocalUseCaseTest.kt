package com.example.feature.training.domain

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetDeckByIdLocalUseCaseTest {

    private val repository: LocalDeckRepository = mockk(relaxed = true)
    private lateinit var getDeckByIdLocalUseCase: GetDeckByIdLocalUseCase

    @BeforeEach
    fun setup() {
        getDeckByIdLocalUseCase = GetDeckByIdLocalUseCase(repository)
    }

    @Test
    fun `should return deck when deck exists in repository`() = runTest {
        val deckId = "deck1"
        val expectedDeck = Deck(
            id = deckId,
            name = "Deck 1",
            isPublic = true,
            authorId = "author1",
            cards = emptyList()
        )
        coEvery { repository.getDeckById(deckId) } returns expectedDeck

        val result = getDeckByIdLocalUseCase(deckId)

        assertThat(result).isEqualTo(expectedDeck)
    }

    @Test
    fun `should throw IllegalStateException when deck does not exist in repository`() = runTest {
        val deckId = "deck2"
        coEvery { repository.getDeckById(deckId) } returns null

        val exception = assertThrows<IllegalStateException> {
            getDeckByIdLocalUseCase(deckId)
        }
        assertThat(exception).hasMessageThat().contains("Deck with id $deckId not found")
    }
}