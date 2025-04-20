package com.example.feature.localdecks.domain.usecase

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDeckByIdUseCaseTest {

    private val repository: LocalDeckRepository = mockk(relaxed = true)
    private lateinit var getDeckByIdUseCase: GetDeckByIdUseCase

    @BeforeEach
    fun setUp() {
        getDeckByIdUseCase = GetDeckByIdUseCase(repository)
    }

    @Test
    fun `should return deck when found`() = runTest {
        val deck = Deck(id = "deck1", name = "Math", isPublic = false, authorId = "user1", cards = emptyList())
        coEvery { repository.getDeckById("deck1") } returns deck

        val result = getDeckByIdUseCase("deck1")

        assertThat(result).isEqualTo(deck)
        coVerify(exactly = 1) { repository.getDeckById("deck1") }
    }

    @Test
    fun `should return null when deck not found`() = runTest {
        coEvery { repository.getDeckById("unknown") } returns null

        val result = getDeckByIdUseCase("unknown")

        assertThat(result).isNull()
        coVerify(exactly = 1) { repository.getDeckById("unknown") }
    }
}