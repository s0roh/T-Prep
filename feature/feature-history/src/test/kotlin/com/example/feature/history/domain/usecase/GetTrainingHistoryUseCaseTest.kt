package com.example.feature.history.domain.usecase

import com.example.database.models.Source
import com.example.database.models.TrainingMode
import com.example.history.domain.entity.TrainingHistory
import com.example.history.domain.entity.TrainingHistoryItem
import com.example.history.domain.repository.HistoryRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTrainingHistoryUseCaseTest {

    private val repository: HistoryRepository = mockk(relaxed = true)
    private lateinit var useCase: GetTrainingHistoryUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetTrainingHistoryUseCase(repository)
    }

    @Test
    fun `should return training history from repository`() = runTest {
        val trainingHistory1 = TrainingHistory(
            id = 1,
            deckId = "deck1",
            deckName = "Deck One",
            cardsCount = 10,
            cardId = 101,
            timestamp = 123456789L,
            isCorrect = true,
            trainingMode = TrainingMode.MULTIPLE_CHOICE,
            source = Source.LOCAL,
            userID = "user1",
            trainingSessionId = "session1"
        )

        val trainingHistory2 = trainingHistory1.copy(id = 2, isCorrect = false, cardId = 102)

        val expected = listOf(
            TrainingHistoryItem(
                timestamp = 123456789L,
                percentOfCorrectAnswers = 50,
                trainingHistories = listOf(trainingHistory1, trainingHistory2)
            )
        )

        coEvery { repository.getTrainingHistory() } returns expected

        val result = useCase()

        assertThat(result).isEqualTo(expected)
        coVerify(exactly = 1) { repository.getTrainingHistory() }
    }

    @Test
    fun `should return empty list if no history exists`() = runTest {
        coEvery { repository.getTrainingHistory() } returns emptyList()

        val result = useCase()

        assertThat(result).isEmpty()
        coVerify(exactly = 1) { repository.getTrainingHistory() }
    }
}