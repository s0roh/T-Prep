package com.example.feature.localdecks.domain.usecase

import com.example.common.domain.entity.Card
import com.example.localdecks.domain.repository.LocalDeckRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InsertCardUseCaseTest {

    private val repository: LocalDeckRepository = mockk(relaxed = true)
    private lateinit var useCase: InsertCardUseCase

    @BeforeEach
    fun setUp() {
        useCase = InsertCardUseCase(repository)
    }

    @Test
    fun `invoke should call repository insertCard with correct arguments`() = runTest {
        val card = Card(
            id = 1,
            question = "What is the capital of France?",
            answer = "Paris",
            wrongAnswers = listOf("London", "Berlin", "Madrid"),
            attachment = null,
            picturePath = null
        )
        val deckId = "deck123"

        useCase(card, deckId)

        coVerify { repository.insertCard(card = card, deckId = deckId) }
    }
}