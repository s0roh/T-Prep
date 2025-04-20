package com.example.feature.localdecks.domain.usecase

import com.example.common.domain.entity.Deck
import com.example.localdecks.domain.repository.LocalDeckRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InsertDeckUseCaseTest {

    private val repository: LocalDeckRepository = mockk(relaxed = true)
    private lateinit var useCase: InsertDeckUseCase

    @BeforeEach
    fun setUp() {
        useCase = InsertDeckUseCase(repository)
    }

    @Test
    fun `invoke should call repository insertDeck`() = runTest {
        val deck = Deck(
            id = "deck1",
            name = "Biology",
            isPublic = false,
            authorId = "user123",
            cards = emptyList()
        )

        useCase(deck)

        coVerify { repository.insertDeck(deck) }
    }
}