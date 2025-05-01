package com.example.feature.training.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.feature.training.R

@Composable
internal fun UserInputWithHighlight(
    userInput: String,
    missingWords: List<String>,
    isCorrect: Boolean,
) {
    Text(
        text = stringResource(R.string.your_answer),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    val annotatedUserInput = buildAnnotatedString {
        val userInputWords = userInput.split("\\s+".toRegex()).filter { it.isNotBlank() }
        var missingWordIndex = 0
        var isAlreadyWrong = false

        if (userInput.isBlank()) {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append(stringResource(R.string.you_dont_answered))
            }
        } else {
            userInputWords.forEachIndexed { index, word ->
                val expectedWord = missingWords.getOrNull(missingWordIndex)

                val color = when {
                    expectedWord == null -> MaterialTheme.colorScheme.error
                    isAlreadyWrong -> MaterialTheme.colorScheme.error
                    isCorrect -> {
                        if (word.equals(expectedWord, ignoreCase = true)) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        }
                    }

                    word.equals(
                        expectedWord,
                        ignoreCase = true
                    ) -> MaterialTheme.colorScheme.primary

                    else -> {
                        isAlreadyWrong = true
                        MaterialTheme.colorScheme.error
                    }
                }

                withStyle(style = SpanStyle(color = color)) {
                    append(word)
                }

                if (index < userInputWords.size - 1) {
                    append(" ")
                }

                missingWordIndex++
            }
        }
    }

    Text(
        text = annotatedUserInput,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}