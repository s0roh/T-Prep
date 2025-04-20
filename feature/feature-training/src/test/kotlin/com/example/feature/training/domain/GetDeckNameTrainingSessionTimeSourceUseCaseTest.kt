package com.example.feature.training.domain

import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class GetDeckNameTrainingSessionTimeSourceUseCaseTest {

    private val repository: TrainingRepository = mockk(relaxed = true)
    private lateinit var useCase: GetDeckNameTrainingSessionTimeSourceUseCase

    @BeforeEach
    fun setup() {
        useCase = GetDeckNameTrainingSessionTimeSourceUseCase(repository)
    }

    @Test
    fun `should return deck name, timestamp, and source when training session history is found`() =
        runTest {
            val trainingSessionId = "session1"
            val expectedDeckName = "Deck 1"
            val expectedTimestamp = 1626181800000L
            val expectedSource = Source.LOCAL

            coEvery { repository.getDeckNameAndTrainingSessionTime(trainingSessionId) } returns Triple(
                expectedDeckName,
                expectedTimestamp,
                expectedSource
            )

            val result = useCase(trainingSessionId)

            assertThat(result).isEqualTo(
                Triple(
                    expectedDeckName,
                    expectedTimestamp,
                    expectedSource
                )
            )
        }

    @Test
    fun `should throw IllegalStateException when training session history is not found`() =
        runTest {
            val trainingSessionId = "session2"
            coEvery { repository.getDeckNameAndTrainingSessionTime(trainingSessionId) } throws IllegalStateException(
                "History with trainingSessionId: $trainingSessionId is not find."
            )

            val exception = assertThrows<IllegalStateException> {
                useCase(trainingSessionId)
            }
            assertThat(exception).hasMessageThat()
                .contains("History with trainingSessionId: $trainingSessionId is not find.")
        }
}