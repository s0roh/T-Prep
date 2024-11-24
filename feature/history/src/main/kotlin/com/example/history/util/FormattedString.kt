package com.example.history.util

import com.example.history.domain.entity.TimePeriod

internal fun TimePeriod.toLocalizedString(): String {
    return when (this) {
        TimePeriod.TODAY -> "Сегодня"
        TimePeriod.YESTERDAY -> "Вчера"
        TimePeriod.THIS_WEEK -> "На этой неделе"
        TimePeriod.THIS_MONTH -> "В этом месяце"
        TimePeriod.THIS_YEAR -> "В этом году"
        TimePeriod.OLDER -> "Ранее"
    }
}
