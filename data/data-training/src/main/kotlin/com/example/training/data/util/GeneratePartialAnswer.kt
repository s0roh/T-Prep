package com.example.training.data.util

/**
 * Функция для генерации частичного ответа для режима "Дополнить ответ".
 * Генерирует строку с пропусками, где определённое количество слов заменяется на "_", чтобы пользователь мог дополнить их в процессе тренировки.
 *
 * Стратегия генерации пропусков:
 * - Пропускаются случайные слова в ответе, но не более, чем определённый процент от общего количества слов,
 *   с ограничением на максимальное количество пропущенных слов.
 * - Пропуски размещаются случайным образом в пределах строки, начиная с случайного индекса.
 *
 * @param answer Ответ, для которого нужно сгенерировать частичный ответ с пропусками.
 * @return `Triple<String, List<String>, Int>`:
 *  - `String`: Частичный ответ, где некоторые слова заменены на "_".
 *  - `List<String>`: Список пропущенных слов.
 *  - `Int`: Индекс в ответе, с которого начинаются пропуски.
 */
internal fun generatePartialAnswer(answer: String): Triple<String, List<String>, Int> {
    val words = answer.split(" ")

    // Если количество слов в ответе меньше минимального для пропусков, возвращаем пустую строку и пустой список
    if (words.size <= MIN_WORDS_FOR_BLANK) return Triple("", words, -1)

    // Рассчитываем максимальное количество пропущенных слов (ограничение по проценту)
    val maxMissingWordsCount = (words.size * MAX_BLANK_PERCENT / 100).coerceIn(1, MAX_MISSING_WORDS)

    // Случайное количество пропущенных слов
    val missingWordCount = (1..maxMissingWordsCount).random()

    // Выбираем случайный индекс для начала пропусков
    val startIndex = (0..(words.size - missingWordCount)).random()

    // Получаем список слов, которые будут пропущены
    val missingWords = words.subList(startIndex, startIndex + missingWordCount)

    // Генерация частичного ответа с пропусками (заменяем слова на "_")
    val partialAnswer = words.toMutableList().apply {
        for (i in startIndex until (startIndex + missingWordCount)) {
            this[i] = "_".repeat(this[i].length) // Заменяем слово на пропуск
        }
    }.joinToString(" ")

    // Возвращаем частичный ответ, список пропущенных слов и индекс начала пропусков
    return Triple(partialAnswer, missingWords, startIndex)
}

private const val MIN_WORDS_FOR_BLANK = 3
private const val MAX_BLANK_PERCENT = 50
private const val MAX_MISSING_WORDS = 5
