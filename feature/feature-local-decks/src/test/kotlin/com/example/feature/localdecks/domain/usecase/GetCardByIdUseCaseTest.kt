package com.example.feature.localdecks.domain.usecase

import com.example.common.domain.entity.Card
import com.example.localdecks.domain.repository.LocalDeckRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetCardByIdUseCaseTest {

    private val repository: LocalDeckRepository = mockk(relaxed = true)
    private lateinit var getCardByIdUseCase: GetCardByIdUseCase

    @BeforeEach
    fun setUp() {
        getCardByIdUseCase = GetCardByIdUseCase(repository)
    }

    @Test
    fun `should return card when found`() = runTest {
        val card = Card(1, "Q", "A")
        coEvery { repository.getCardById(1) } returns card

        val result = getCardByIdUseCase(1)

        assertThat(result).isEqualTo(card)
        coVerify(exactly = 1) { repository.getCardById(1) }
    }

    @Test
    fun `should return null when card not found`() = runTest {
        coEvery { repository.getCardById(2) } returns null

        val result = getCardByIdUseCase(2)

        assertThat(result).isNull()
        coVerify(exactly = 1) { repository.getCardById(2) }
    }
}