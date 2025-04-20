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

class LikeOrUnlikeUseCaseTest {

    private val repository: PublicDeckRepository = mockk(relaxed = true)
    private lateinit var useCase: LikeOrUnlikeUseCase

    @BeforeEach
    fun setUp() {
        useCase = LikeOrUnlikeUseCase(repository)
    }

    @Test
    fun `should call repository and return updated likes count when liking deck`() = runTest {
        val deckId = "deck123"
        val isLiked = false
        val expectedLikes = 10

        coEvery { repository.likeOrUnlikeDeck(deckId, isLiked) } returns expectedLikes

        val result = useCase(deckId, isLiked)

        assertThat(result).isEqualTo(expectedLikes)
        coVerify(exactly = 1) { repository.likeOrUnlikeDeck(deckId, isLiked) }
    }

    @Test
    fun `should call repository and return updated likes count when unliking deck`() = runTest {
        val deckId = "deck123"
        val isLiked = true
        val expectedLikes = 8

        coEvery { repository.likeOrUnlikeDeck(deckId, isLiked) } returns expectedLikes

        val result = useCase(deckId, isLiked)

        assertThat(result).isEqualTo(expectedLikes)
        coVerify(exactly = 1) { repository.likeOrUnlikeDeck(deckId, isLiked) }
    }

    @Test
    fun `should throw exception when repository throws`() = runTest {
        val deckId = "deck123"
        val isLiked = false
        val exception = Exception("Network error")

        coEvery { repository.likeOrUnlikeDeck(deckId, isLiked) } throws exception

        val thrown = assertThrows<Exception> {
            useCase(deckId, isLiked)
        }

        assertThat(thrown).isSameInstanceAs(exception)
        coVerify(exactly = 1) { repository.likeOrUnlikeDeck(deckId, isLiked) }
    }
}