package com.example.feature.training.domain

import android.net.Uri
import com.example.database.models.Source
import com.example.training.domain.repository.TrainingRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetCardPictureUseCaseTest {

    private val repository: TrainingRepository = mockk(relaxed = true)
    private lateinit var getCardPictureUseCase: GetCardPictureUseCase

    @BeforeEach
    fun setup() {
        getCardPictureUseCase = GetCardPictureUseCase(repository)
    }

    @Test
    fun `should return Uri when repository returns valid Uri`() = runTest {
        val expectedUri = mockk<Uri>()
        coEvery {
            repository.getCardPicture(
                deckId = "deck1",
                cardId = 1,
                source = Source.LOCAL,
                attachment = null
            )
        } returns expectedUri

        val result = getCardPictureUseCase("deck1", 1, Source.LOCAL, null)

        assertThat(result).isEqualTo(expectedUri)
    }

    @Test
    fun `should return null when repository returns null`() = runTest {
        coEvery {
            repository.getCardPicture(
                deckId = "deck1",
                cardId = 1,
                source = Source.LOCAL,
                attachment = null
            )
        } returns null

        val result = getCardPictureUseCase("deck1", 1, Source.LOCAL, null)

        assertThat(result).isNull()
    }

    @Test
    fun `should return Uri when source is NETWORK and picture is successfully fetched`() = runTest {
        val expectedUri = mockk<Uri>()
        coEvery {
            repository.getCardPicture(
                deckId = "deck1",
                cardId = 2,
                source = Source.NETWORK,
                attachment = "attachment.jpg"
            )
        } returns expectedUri

        val result = getCardPictureUseCase("deck1", 2, Source.NETWORK, "attachment.jpg")

        assertThat(result).isEqualTo(expectedUri)
    }

    @Test
    fun `should return null when attachment is null for NETWORK source`() = runTest {
        coEvery {
            repository.getCardPicture(
                deckId = "deck1",
                cardId = 3,
                source = Source.NETWORK,
                attachment = null
            )
        } returns null

        val result = getCardPictureUseCase("deck1", 3, Source.NETWORK, null)

        assertThat(result).isNull()
    }
}