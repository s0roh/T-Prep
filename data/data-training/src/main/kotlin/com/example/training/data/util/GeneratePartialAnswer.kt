package com.example.training.data.util

/**
 * Функция для генерации частичного ответа для режима "Дополнить ответ".
 * Генерирует строку с пропусками, где определённое количество слов заменяется на "...",
 * чтобы пользователь мог дополнить их в процессе тренировки.
 *
 * Стратегия генерации пропусков:
 * - Пропускаются случайные слова в ответе, но не более, чем определённый процент от общего количества слов,
 *   с ограничением на максимальное количество пропущенных слов.
 * - Пропуски размещаются случайным образом в пределах строки, начиная с случайного индекса.
 *
 * @param answer Ответ, для которого нужно сгенерировать частичный ответ с пропусками.
 * @return Пара:
 *  - `String`: Частичный ответ, где некоторые слова заменены на "...".
 *  - `List<String>`: Список пропущенных слов.
 */
internal fun generatePartialAnswer(answer: String): Pair<String, List<String>> {
    val words = answer.split(" ")
    if (words.size <= MIN_WORDS_FOR_BLANK) return "" to words

    // Рассчитываем максимальное количество пропущенных слов
    val maxMissingWordsCount = (words.size * MAX_BLANK_PERCENT / 100).coerceIn(1, MAX_MISSING_WORDS)

    // Случайное количество пропущенных слов
    val missingWordCount = (1..maxMissingWordsCount).random()

    // Выбираем случайный индекс для начала пропусков
    val startIndex = (0..(words.size - missingWordCount)).random()

    // Получаем слова, которые будут пропущены
    val missingWords = words.subList(startIndex, startIndex + missingWordCount)

    // Генерация частичного ответа с пропусками
    val partialAnswer = words.toMutableList().apply {
        for (i in startIndex until (startIndex + missingWordCount)) {
            //this[i] = "..."
            this[i] = "_".repeat(this[i].length)
        }
    }.joinToString(" ")

    return partialAnswer to missingWords
}

private const val MIN_WORDS_FOR_BLANK = 3
private const val MAX_BLANK_PERCENT = 50
private const val MAX_MISSING_WORDS = 5
