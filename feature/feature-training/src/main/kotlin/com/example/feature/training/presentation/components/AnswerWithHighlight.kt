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
internal fun AnswerWithHighlight(answer: String, missingWords: List<String>, startIndex: Int) {
    Text(
        text = stringResource(R.string.answer),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // Разбиваем ответ на отдельные слова (игнорируя пробелы)
    val words = answer.split("\\s+".toRegex()).filter { it.isNotBlank() }

    // Проверяем, что startIndex и missingWords корректны
    require(startIndex >= 0 && startIndex + missingWords.size <= words.size) {
        "Invalid startIndex ($startIndex) or missingWords size (${missingWords.size}). " +
                "Expected: startIndex >= 0 and startIndex + missingWords.size (${startIndex + missingWords.size}) " +
                "<= words.size (${words.size}). " +
                "Actual words: ${words.size}, requested highlight range: $startIndex..${startIndex + missingWords.size - 1}"
    }

    val annotatedAnswer = buildAnnotatedString {
        words.forEachIndexed { index, word ->
            // Добавляем пробел перед словом, кроме первого
            if (index > 0) {
                append(" ")
            }

            if (index >= startIndex && index < startIndex + missingWords.size) {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(word)
                }
            } else {
                append(word)
            }
        }
    }

    Text(
        text = annotatedAnswer,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 40.dp)
    )
}