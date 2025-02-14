package com.example.training.data.util

import java.text.Normalizer

/**
 * Нормализация текста.
 *
 * Функция приводит текст к нижнему регистру и удаляет все ненужные символы,
 * оставляя только буквы, цифры и пробелы. Она используется для унификации текста,
 * чтобы исключить различия, связанные с регистром и лишними символами.
 *
 * Шаги нормализации:
 * 1. Приведение текста к нижнему регистру.
 * 2. Удаление всех символов, которые не являются буквами, цифрами или пробелами.
 * 3. Приведение строки к нормализованной форме NFD.
 *
 * @param text Текст, который необходимо нормализовать.
 * @return Нормализованный текст, приведённый к нижнему регистру и без лишних символов.
 */
internal fun normalizeText(text: String): String {
    val normalized = Normalizer.normalize(text.lowercase().trim(), Normalizer.Form.NFD)
    return normalized.replace(Regex("[^\\p{L}\\d ]"), "")
}
