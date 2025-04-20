package com.example.feature.profile.domain

import com.example.decks.domain.repository.PublicDeckRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetFavouriteDecksIdsUseCaseTest {

    private val repository: PublicDeckRepository = mockk(relaxed = true)
    private lateinit var useCase: GetFavouriteDecksIdsUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetFavouriteDecksIdsUseCase(repository)
    }

    @Test
    fun `should return list of favourite deck ids from repository`() = runTest {
        val expectedIds = listOf("deck1", "deck2", "deck3")
        coEvery { repository.getFavouriteDeckIds() } returns expectedIds

        val result = useCase()

        assertThat(result).isEqualTo(expectedIds)
        coVerify(exactly = 1) { repository.getFavouriteDeckIds() }
    }

    @Test
    fun `should return empty list if repository returns empty`() = runTest {
        coEvery { repository.getFavouriteDeckIds() } returns emptyList()

        val result = useCase()

        assertThat(result).isEmpty()
        coVerify(exactly = 1) { repository.getFavouriteDeckIds() }
    }

    @Test
    fun `should throw exception if repository throws`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { repository.getFavouriteDeckIds() } throws exception

        val thrown = assertThrows<RuntimeException> {
            useCase()
        }
        assertThat(thrown).isSameInstanceAs(exception)
        coVerify(exactly = 1) { repository.getFavouriteDeckIds() }
    }
}