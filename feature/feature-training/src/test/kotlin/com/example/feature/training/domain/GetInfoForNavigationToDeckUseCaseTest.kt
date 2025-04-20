package com.example.feature.training.domain

import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetInfoForNavigationToDeckUseCaseTest {

    private val repository: TrainingRepository = mockk(relaxed = true)
    private lateinit var useCase: GetInfoForNavigationToDeckUseCase

    @BeforeEach
    fun setup() {
        useCase = GetInfoForNavigationToDeckUseCase(repository)
    }

    @Test
    fun `should return deckId and source when training history exists`() = runTest {
        val trainingSessionId = "session1"
        val expectedDeckId = "deck123"
        val expectedSource = Source.LOCAL

        coEvery {
            repository.getInfoForNavigationToDeck(trainingSessionId)
        } returns (expectedDeckId to expectedSource)

        val result = useCase(trainingSessionId)

        assertThat(result.first).isEqualTo(expectedDeckId)
        assertThat(result.second).isEqualTo(expectedSource)

        coVerify(exactly = 1) {
            repository.getInfoForNavigationToDeck(trainingSessionId)
        }
    }

    @Test
    fun `should throw exception when training history not found`() = runTest {
        val trainingSessionId = "nonexistent"
        coEvery {
            repository.getInfoForNavigationToDeck(trainingSessionId)
        } throws IllegalStateException("History with trainingSessionId: $trainingSessionId is not find.")

        val exception = assertThrows<IllegalStateException> {
            useCase(trainingSessionId)
        }

        assertThat(exception).hasMessageThat()
            .contains("History with trainingSessionId: $trainingSessionId is not find.")
    }
}