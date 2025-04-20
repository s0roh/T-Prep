package com.example.feature.training.domain

import com.example.training.domain.repository.TrainingRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTotalAndCorrectCountAnswersUseCaseTest {

    private val repository: TrainingRepository = mockk(relaxed = true)
    private lateinit var useCase: GetTotalAndCorrectCountAnswersUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetTotalAndCorrectCountAnswersUseCase(repository)
    }

    @Test
    fun `should return total and correct count of answers`() = runTest {
        val sessionId = "session123"
        val expected = 10 to 7

        coEvery { repository.getTotalAndCorrectCountAnswers(sessionId) } returns expected

        val result = useCase(sessionId)

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should return zero values when no answers recorded`() = runTest {
        val sessionId = "session_empty"
        val expected = 0 to 0

        coEvery { repository.getTotalAndCorrectCountAnswers(sessionId) } returns expected

        val result = useCase(sessionId)

        assertThat(result).isEqualTo(expected)
    }
}