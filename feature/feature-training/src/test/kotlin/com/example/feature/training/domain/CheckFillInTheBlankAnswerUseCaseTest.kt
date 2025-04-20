package com.example.feature.training.domain

import com.example.training.domain.repository.TrainingRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CheckFillInTheBlankAnswerUseCaseTest {

    private val repository: TrainingRepository = mockk(relaxed = true)
    private lateinit var checkFillInTheBlankAnswerUseCase: CheckFillInTheBlankAnswerUseCase

    @BeforeEach
    fun setUp() {
        checkFillInTheBlankAnswerUseCase = CheckFillInTheBlankAnswerUseCase(repository)
    }

    @ParameterizedTest
    @CsvSource(
        "correct, correct, true",
        "correkt, correct, false",
        "'this is correct answer', 'this is correct answer', true",
        "'this is corect answer', 'this is correct answer', true",
        "'this is incorekt anser', 'this is correct answer', false",
        "short, 'this is correct answer', false"
    )
    fun `test fill-in-the-blank answer checking`(
        userInput: String,
        correctWordsRaw: String,
        expected: Boolean
    ) = runTest {
        val correctWords = correctWordsRaw.split(" ")

        coEvery { repository.checkFillInTheBlankAnswer(userInput, correctWords) } returns expected

        val actual = checkFillInTheBlankAnswerUseCase(userInput, correctWords)

        assertThat(actual).isEqualTo(expected)
        coVerify(exactly = 1) { repository.checkFillInTheBlankAnswer(userInput, correctWords) }
    }
}