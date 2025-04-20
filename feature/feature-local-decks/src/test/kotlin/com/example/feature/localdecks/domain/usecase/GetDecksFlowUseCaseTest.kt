package com.example.feature.localdecks.domain.usecase

import app.cash.turbine.test
import com.example.common.ui.entity.DeckUiModel
import com.example.localdecks.domain.repository.LocalDeckRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDecksFlowUseCaseTest {

    private val repository: LocalDeckRepository = mockk(relaxed = true)
    private lateinit var getDecksFlowUseCase: GetDecksFlowUseCase

    @BeforeEach
    fun setUp() {
        getDecksFlowUseCase = GetDecksFlowUseCase(repository)
    }

    @Test
    fun `invoke should return a flow of decks`() = runTest {
        val decks = listOf(
            DeckUiModel("1", "Deck One", false, false, false, 5, 10, 3),
            DeckUiModel("2", "Deck Two", true, true, true, 12, 50, 8)
        )
        every { repository.getDecks() } returns flowOf(decks)

        getDecksFlowUseCase().test {
            val emitted = awaitItem()
            assertThat(emitted).isEqualTo(decks)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return an empty list when no decks`() = runTest {
        every { repository.getDecks() } returns flowOf(emptyList())

        getDecksFlowUseCase().test {
            val emitted = awaitItem()
            assertThat(emitted).isEmpty()
            awaitComplete()
        }
    }
}