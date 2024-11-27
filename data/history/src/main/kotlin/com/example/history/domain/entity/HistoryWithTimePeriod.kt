package com.example.history.domain.entity

data class HistoryWithTimePeriod(
    val timePeriod: TimePeriod,
    val trainingHistories: List<TrainingHistory>
)

enum class TimePeriod {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    OLDER
}