package com.example.feature.reminder.domain

import com.example.data.reminder.domain.repository.ReminderScheduler
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class GetTrainingPlanUseCaseTest {

    private val reminderScheduler: ReminderScheduler = mockk(relaxed = true)
    private lateinit var useCase: GetTrainingPlanUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetTrainingPlanUseCase(reminderScheduler)
    }

    @Test
    fun `should return training plan for given date range and time`() = runTest {
        val startDate = 20230401
        val finishDate = 20230430
        val preferredTime = 1200
        val expectedTrainingPlan =
            listOf(1609459200000L, 1609545600000L)

        coEvery {
            reminderScheduler.getTrainingPlan(
                startDate,
                finishDate,
                preferredTime
            )
        } returns expectedTrainingPlan

        val result = useCase(startDate, finishDate, preferredTime)

        assertThat(result).isEqualTo(expectedTrainingPlan)
        coVerify(exactly = 1) {
            reminderScheduler.getTrainingPlan(
                startDate,
                finishDate,
                preferredTime
            )
        }
    }

    @Test
    fun `should return empty list when no training plan is found`() = runTest {
        val startDate = 20230401
        val finishDate = 20230430
        val preferredTime = 1200
        val expectedTrainingPlan: List<Long> = emptyList()

        coEvery {
            reminderScheduler.getTrainingPlan(
                startDate,
                finishDate,
                preferredTime
            )
        } returns expectedTrainingPlan

        val result = useCase(startDate, finishDate, preferredTime)

        assertThat(result).isEqualTo(expectedTrainingPlan)
        coVerify(exactly = 1) {
            reminderScheduler.getTrainingPlan(
                startDate,
                finishDate,
                preferredTime
            )
        }
    }
}