package com.example.feature.reminder.domain

import com.example.data.reminder.domain.entity.Reminder
import com.example.data.reminder.domain.repository.ReminderScheduler
import com.example.database.models.Source
import io.mockk.coEvery
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InsertReminderUseCaseTest {

    private val reminderScheduler: ReminderScheduler = mockk(relaxed = true)
    private lateinit var useCase: InsertReminderUseCase

    @BeforeEach
    fun setUp() {
        useCase = InsertReminderUseCase(reminderScheduler)
    }

    @Test
    fun `should insert reminder and return ID`() = runTest {
        val reminder = Reminder(
            id = 0L,
            reminderTime = 1_700_000_000_000L,
            name = "Morning training",
            source = Source.LOCAL,
            deckId = "deck123"
        )

        coEvery { reminderScheduler.insertReminder(reminder) } returns 42L

        val result = useCase(reminder)

        assertThat(result).isEqualTo(42L)
        coVerify(exactly = 1) { reminderScheduler.insertReminder(reminder) }
    }
}