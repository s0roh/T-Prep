package com.example.feature.training.domain

import com.example.training.domain.repository.TrainingRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetNextTrainingTimeUseCaseTest {

    private val repository: TrainingRepository = mockk(relaxed = true )
    private lateinit var useCase: GetNextTrainingTimeUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetNextTrainingTimeUseCase(repository)
    }

    @Test
    fun `should return next training time when it exists`() = runTest {
        val sessionId = "session123"
        val expectedTime = 1_717_000_000_000L

        coEvery { repository.getNextTrainingTime(sessionId) } returns expectedTime

        val result = useCase(sessionId)

        assertThat(result).isEqualTo(expectedTime)
    }

    @Test
    fun `should return null when next training time does not exist`() = runTest {
        val sessionId = "session123"

        coEvery { repository.getNextTrainingTime(sessionId) } returns null

        val result = useCase(sessionId)

        assertThat(result).isNull()
    }

    @Test
    fun `should throw exception when training history not found`() = runTest {
        val sessionId = "missing-session"
        val expectedMessage = "History with trainingSessionId: $sessionId is not find."

        coEvery { repository.getNextTrainingTime(sessionId) } throws IllegalStateException(expectedMessage)

        val exception = assertThrows<IllegalStateException> {
            useCase(sessionId)
        }

        assertThat(exception).hasMessageThat().contains(expectedMessage)
    }
}