package com.example.feature.reminder.presentation.util

import java.util.Calendar
import java.util.TimeZone

/**
 * Корректирует даты начала и конца тренировок.
 * Если дата начала - сегодня, устанавливает текущее время + 1 минута.
 * В противном случае использует предпочитаемое время.
 * Дата окончания всегда устанавливается в 23:59.
 * Возвращает Triple(adjustedStartDate, adjustedEndDate, adjustedPreferredTime).
 */
internal fun calculateAdjustedDates(
    startDate: Long,
    endDate: Long,
    preferredTime: Long,
): Triple<Long, Long, Int> {
    val currentTimeMillis = System.currentTimeMillis()

    val startOfToday = Calendar.getInstance().apply {
        timeInMillis = currentTimeMillis
        set(Calendar.HOUR_OF_DAY, ZERO_HOUR)
        set(Calendar.MINUTE, ZERO_MINUTE)
        set(Calendar.SECOND, ZERO_SECOND)
        set(Calendar.MILLISECOND, ZERO_MILLISECOND)
    }.timeInMillis

    val adjustedStartDate = if (startDate in startOfToday until startOfToday + DAY_IN_MILLIS) {
        // Если дата начала — сегодня, ставим текущее время + 1 минута
        currentTimeMillis + MINUTE_IN_MILLIS
    } else {
        // В противном случае используем предпочитаемое время
        setTimeToPreferred(startDate, preferredTime)
    }

    // Дата окончания всегда в 23:59
    val adjustedEndDate = setTimeToEndOfDay(endDate)

    // Преобразуем предпочитаемое время в секунды от начала дня
    val adjustedPreferredTime = convertToSeconds(preferredTime)

    return Triple(adjustedStartDate, adjustedEndDate, adjustedPreferredTime)
}

/**
 * Устанавливает в переданной дате `date` только часы и минуты из `preferredTime`.
 */
private fun setTimeToPreferred(date: Long, preferredTime: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = date

        // Извлекаем только часы и минуты из preferredTime
        val preferredCalendar = Calendar.getInstance().apply {
            timeInMillis = preferredTime
        }

        val hours = preferredCalendar.get(Calendar.HOUR_OF_DAY)
        val minutes = preferredCalendar.get(Calendar.MINUTE)

        // Устанавливаем часы и минуты на оригинальную дату
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, ZERO_SECOND)
        set(Calendar.MILLISECOND, ZERO_MILLISECOND)
    }
    return calendar.timeInMillis
}

/**
 * Устанавливает дату `date` на 23:59.
 */
private fun setTimeToEndOfDay(date: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = date
        set(Calendar.HOUR_OF_DAY, END_OF_DAY_HOUR)
        set(Calendar.MINUTE, END_OF_DAY_MINUTE)
        set(Calendar.SECOND, ZERO_SECOND)
        set(Calendar.MILLISECOND, ZERO_MILLISECOND)
    }.timeInMillis
}

/**
 * Преобразует `preferredTime` в секунды с начала дня.
 */
private fun convertToSeconds(time: Long): Int {
    // Устанавливаем время в календаре с учетом UTC
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = time
    }

    // Получаем часы и минуты в UTC+0
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)

    // Возвращаем количество секунд с начала дня
    return hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE
}

private const val MINUTE_IN_MILLIS = 60 * 1000L
private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
private const val ZERO_HOUR = 0
private const val ZERO_MINUTE = 0
private const val ZERO_SECOND = 0
private const val ZERO_MILLISECOND = 0
private const val END_OF_DAY_HOUR = 23
private const val END_OF_DAY_MINUTE = 59
private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_MINUTE = 60
