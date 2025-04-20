package com.example.feature.reminder.domain

import com.example.data.reminder.domain.entity.Reminder
import com.example.data.reminder.domain.repository.ReminderScheduler
import com.example.database.models.Source
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetRemindersForDeckUseCaseTest {

    private val reminderScheduler: ReminderScheduler = mockk(relaxed = true)
    private lateinit var useCase: GetRemindersForDeckUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetRemindersForDeckUseCase(reminderScheduler)
    }

    @Test
    fun `should return list of reminders for deck`() = runTest {
        val deckId = "deck123"
        val source = Source.LOCAL
        val expectedReminders = listOf(
            Reminder(
                id = 1,
                reminderTime = 1_700_000_000_000L,
                name = "Morning training",
                source = Source.LOCAL,
                deckId = "deck123"
            ),
            Reminder(
                id = 2,
                reminderTime = 1_700_500_000_000L,
                name = "Evening training",
                source = Source.LOCAL,
                deckId = "deck123"
            )
        )

        coEvery { reminderScheduler.getRemindersForDeck(deckId, source) } returns expectedReminders

        val result = useCase(deckId, source)

        assertThat(result).isEqualTo(expectedReminders)
        coVerify(exactly = 1) { reminderScheduler.getRemindersForDeck(deckId, source) }
    }

    @Test
    fun `should return empty list when no reminders found`() = runTest {
        val deckId = "deck123"
        val source = Source.LOCAL
        val expectedReminders: List<Reminder> = emptyList()

        coEvery { reminderScheduler.getRemindersForDeck(deckId, source) } returns expectedReminders

        val result = useCase(deckId, source)

        assertThat(result).isEqualTo(expectedReminders)
        coVerify(exactly = 1) { reminderScheduler.getRemindersForDeck(deckId, source) }
    }
}