package com.example.feature.training.domain

import com.example.database.models.TrainingMode
import com.example.training.domain.entity.TrainingError
import com.example.training.domain.repository.TrainingRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetErrorsListUseCaseTest {
    private val repository: TrainingRepository = mockk(relaxed = true)
    private lateinit var useCase: GetErrorsListUseCase

    @BeforeEach
    fun setup() {
        useCase = GetErrorsListUseCase(repository)
    }

    @Test
    fun `should return list of errors when errors exist for training session`() = runTest {
        val trainingSessionId = "session1"
        val expectedErrors = listOf(
            TrainingError(
                id = 1,
                cardId = 101,
                deckId = "deck1",
                trainingSessionId = trainingSessionId,
                trainingSessionTime = 1626181800000L,
                question = "What is Kotlin?",
                answer = "A programming language",
                blankAnswer = null,
                userAnswer = "A language for JVM",
                trainingMode = TrainingMode.FILL_IN_THE_BLANK,
                attachment = null
            ),
            TrainingError(
                id = 2,
                cardId = 102,
                deckId = "deck1",
                trainingSessionId = trainingSessionId,
                trainingSessionTime = 1626181900000L,
                question = "What is Java?",
                answer = "A programming language",
                blankAnswer = null,
                userAnswer = "A language for Android",
                trainingMode = TrainingMode.MULTIPLE_CHOICE,
                attachment = "java_image.jpg"
            )
        )

        coEvery { repository.getErrorsList(trainingSessionId) } returns expectedErrors

        val result = useCase(trainingSessionId)

        assertThat(result).isEqualTo(expectedErrors)
    }

    @Test
    fun `should return empty list when no errors exist for training session`() = runTest {
        val trainingSessionId = "session2"
        val expectedErrors = emptyList<TrainingError>()

        coEvery { repository.getErrorsList(trainingSessionId) } returns expectedErrors

        val result = useCase(trainingSessionId)

        assertThat(result).isEqualTo(expectedErrors)
    }
}