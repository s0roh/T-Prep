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
    // Строим аннотированную строку, подсвечивая только пропущенные слова
    val annotatedAnswer = buildAnnotatedString {
        val answerText = answer
        var currentIndex = 0 // Индекс для отслеживания позиции в строке ответа
        var currentStartIndex = startIndex // Начальный индекс для пропусков

        // Перебираем все пропущенные слова
        for (missingWord in missingWords) {
            // Ищем первое вхождение пропущенного слова начиная с текущей позиции
            var startIndex = answerText.indexOf(missingWord, currentStartIndex)

            // Если нашли пропущенное слово, добавляем все текст до этого слова
            if (startIndex != -1) {
                append(answerText.substring(currentIndex, startIndex))

                // Подсвечиваем найденное пропущенное слово
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(missingWord)
                }

                // Обновляем текущий индекс, чтобы продолжить поиск с места после пропущенного слова
                currentIndex = startIndex + missingWord.length
                currentStartIndex = currentIndex
            }
        }

        // Добавляем оставшуюся часть текста, которая не была подсвечена
        append(answerText.substring(currentIndex))
    }

    Text(
        text = annotatedAnswer,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 40.dp)
    )
}